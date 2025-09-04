package az.company.bookappbackend.user.service;

import az.company.bookappbackend.follows.entities.BlockEntity;
import az.company.bookappbackend.follows.entities.FollowEntity;
import az.company.bookappbackend.follows.entities.FollowRequestEntity;
import az.company.bookappbackend.follows.repository.BlockRepository;
import az.company.bookappbackend.follows.repository.FollowRequestRepository;
import az.company.bookappbackend.minio.service.MinioStorageService;
import az.company.bookappbackend.user.dto.FollowerEvent;
import az.company.bookappbackend.user.dto.request.EditUserInfoRequest;
import az.company.bookappbackend.user.dto.response.*;
import az.company.bookappbackend.user.entity.UserEntity;
import az.company.bookappbackend.user.exceptions.UserAvatarAlreadyEmptyException;
import az.company.bookappbackend.user.exceptions.UserNotFoundException;
import az.company.bookappbackend.user.exceptions.UsernameAlreadyExists;
import az.company.bookappbackend.user.mapper.UserMapper;
import az.company.bookappbackend.follows.repository.FollowRepository;
import az.company.bookappbackend.user.repository.UserRepository;
import io.minio.GetObjectResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final MinioStorageService minioStorageService;
    private final UserMapper userMapper;
    private final FollowRequestRepository followRequestRepository;
    private final BlockRepository blockRepository;
    private final ApplicationEventPublisher publisher;

    public UserProfileResponse getUserProfile(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        //TODO: implement fetching of these data when Follow relations are implemented
        Long followersCount = 0L;
        Long followingCount = 0L;
        Long activitiesCount = 0L;

        return userMapper.toUserProfileResponse(userEntity, followersCount, followingCount, activitiesCount);
    }

    public Page<SimpleUserProfileDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toSimpleUserProfileDto);
    }

    @Transactional
    public UpdatedUserProfileDto updateUserProfile(Long userId, EditUserInfoRequest editUserInfoRequest) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        if (userRepository.existsByUsername(userEntity.getUsername())) {
            throw new UsernameAlreadyExists("Username already exists with username " + userEntity.getUsername());
        }

        UserEntity updatedUserEntity = userMapper.updateUserEntity(userEntity, editUserInfoRequest);
        return userMapper.toUpdatedUserProfileDto(userRepository.save(updatedUserEntity));
    }

    @Transactional
    public boolean updateUserVisibility(Long userId, boolean isPrivate) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        userEntity.setPrivate(isPrivate);

        return userRepository.save(userEntity).isPrivate();
    }

    //This method handles both creation and update of the avatar photo
    @Transactional
    public String uploadUserAvatar(Long userId, MultipartFile file) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        // If user photo exists, it deletes old one to optimize storage
        if (userEntity.getAvatarUrl() != null) {
            deleteUserAvatar(userId);
        }

        String avatarUrl = minioStorageService.uploadProfilePhoto(userEntity.getUsername(), file);
        userEntity.setAvatarUrl(avatarUrl);
        userRepository.save(userEntity);

        return avatarUrl;
    }

    public UserAvatarResponse getUserAvatar(Long userId) {
        String avatarUrl = userRepository.findAvatarUrlByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        GetObjectResponse profilePhotoFile = minioStorageService.getProfilePhotoFile(avatarUrl);

        return new UserAvatarResponse(
                avatarUrl,
                profilePhotoFile.headers().get("Content-Type"),
                profilePhotoFile
        );
    }

    @Transactional
    public void deleteUserAvatar(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        if (userEntity.getAvatarUrl() == null) {
            throw new UserAvatarAlreadyEmptyException("User avatar is already empty with id " + userId);
        }

        minioStorageService.deleteProfilePhoto(userEntity.getAvatarUrl());

        userEntity.setAvatarUrl(null);
        userRepository.save(userEntity);
    }

    // Follow Endpoints
    @Transactional
    public FollowingResponseDTO followUser(
            @NotNull @Min(1) Long userID,
            Authentication authentication
    ) {
        String userName = authentication.getName();
        UserEntity currentUser = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException("User not found with name " + userName));

        UserEntity userToFollow = userRepository.findById(userID)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userID));

        if (blockRepository.findByBlockerAndBlockedUser(currentUser.getId(), userID).isEmpty()) {
            throw new RuntimeException("The user is blocked by you.");
        }

        if (currentUser.getId().equals(userID)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        Optional<FollowEntity> existingFollow = followRepository.findByFollowerIdAndFolloweeId(currentUser.getId(), userID);

        if (existingFollow.isPresent()) {
            throw new RuntimeException("You are already following this user");
        }

        if (userToFollow.isPrivate()) {
            Optional<FollowRequestEntity> existingRequest = followRequestRepository.findByFromUserIdAndToUserId(currentUser.getId(), userID);
            if (existingRequest.isPresent()) {
                throw new RuntimeException("You have already sent a follow request to this user");
            }

            FollowRequestEntity followRequestEntity = FollowRequestEntity.builder()
                    .fromUser(currentUser)
                    .toUser(userToFollow)
                    .requestedAt(Instant.now())
                    .build();

            followRequestRepository.save(followRequestEntity);

            return userMapper.toFollowingResponseDTO(userToFollow);
        }

        publisher.publishEvent(new FollowerEvent(
                this,
                userToFollow.getId(),
                currentUser.getId()
        ));

        FollowEntity followEntity = FollowEntity.builder()
                .follower(currentUser)
                .followee(userToFollow)
                .createdAt(Instant.now())
                .build();

        followRepository.save(followEntity);
        Long myFollowingCount = currentUser.getFollowingCount();
        currentUser.setFollowingCount(myFollowingCount + 1);

        Long hisFollowerCount = userToFollow.getFollowersCount();
        userToFollow.setFollowersCount(hisFollowerCount + 1);

        return userMapper.toFollowingResponseDTO(userToFollow);
    }

    @Transactional
    public FollowingResponseDTO unfollowUser(@NotNull @Min(1) Long userId, Authentication authentication) {
        UserEntity unfollowedUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        String userName = authentication.getName();
        UserEntity currentUser= userRepository.findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException("User not found with username " + userName));

        Optional<FollowEntity> followEntityOptional = followRepository.findByFollowerIdAndFolloweeId(currentUser.getId(), userId);

        if (followEntityOptional.isEmpty()) {
            throw new RuntimeException("You are not following this user");
        }

        followRepository.delete(followEntityOptional.get());

        Long myFollowingCount = currentUser.getFollowingCount();
        currentUser.setFollowingCount(myFollowingCount - 1);

        Long hisFollowerCount = unfollowedUser.getFollowersCount();
        unfollowedUser.setFollowersCount(hisFollowerCount - 1);

        return userMapper.toFollowingResponseDTO(unfollowedUser);
    }

    public Page<FollowingResponseDTO> getFollowers(Long userID, Pageable pageable) {

        ArrayList<FollowingResponseDTO> content = followRepository.findUserFollowersByUserID(userID, pageable).stream()
                .map(
                        follow -> userMapper.toFollowingResponseDTO(follow.getFollower())
                )
                .collect(
                        ArrayList<FollowingResponseDTO>::new,
                        ArrayList::add,
                        ArrayList::addAll
                );

        return new PageImpl<>(
                content,
                pageable,
                followRepository.count()
        );
    }

    public Page<FollowingResponseDTO> getFollowings(Long userID, Pageable pageable) {

        ArrayList<FollowingResponseDTO> content = followRepository.findUserFolloweesByUserID(userID, pageable).stream()
                .map(
                        follow -> userMapper.toFollowingResponseDTO(follow.getFollower())
                )
                .collect(
                        ArrayList<FollowingResponseDTO>::new,
                        ArrayList::add,
                        ArrayList::addAll
                );

        return new PageImpl<>(
                content,
                pageable,
                followRepository.count()
        );
    }

    public Page<FollowingResponseDTO> getUserFollowers(Pageable pageable, Authentication authentication) {
        String userName = authentication.getName();
        Long currentUserId = userRepository.userIdByUsername(userName).orElseThrow(
                () -> new UserNotFoundException("User not found with username " + userName)
        );
        return getFollowers(currentUserId, pageable);
    }

    public Page<FollowingResponseDTO> getUserFollowings(Pageable pageable, Authentication authentication) {
        String userName = authentication.getName();
        Long currentUserId = userRepository.userIdByUsername(userName).orElseThrow(
                () -> new UserNotFoundException("User not found with username " + userName)
        );

        return getFollowers(currentUserId, pageable);
    }

    public Page<FollowRequestResponseDTO> getOutgoingFollowRequests(Pageable pageable, Authentication authentication) {
        String userName = authentication.getName();
        Long currentUserId = userRepository.userIdByUsername(userName).orElseThrow(
                () -> new UserNotFoundException("User not found with username " + userName)
        );

        Page<FollowRequestEntity> allByFromID = followRequestRepository.findAllOutgoingFollowRequestsOfUser(
                currentUserId,
                pageable
        );

        return allByFromID.map(userMapper::toFollowRequestResponseDTO);
    }

    public Page<FollowRequestResponseDTO> getIncomingFollowRequests(Pageable pageable, Authentication authentication) {
        String userName = authentication.getName();
        Long currentUserId = userRepository.userIdByUsername(userName).orElseThrow(
                () -> new UserNotFoundException("User not found with username " + userName)
        );

        Page<FollowRequestEntity> allByFromID = followRequestRepository.findAllIncomingFollowRequestsOfUser(currentUserId, pageable);
        return allByFromID.map(userMapper::toFollowRequestResponseDTO);
    }

    public Void cancelOutgoingFollowRequest(Long reqID) {
        followRequestRepository.deleteById(reqID);
        return null;
    }

    @Transactional
    public Void acceptFollowRequest(Long reqID, Authentication authentication) {

        String userName = authentication.getName();
        Long currentUserId = userRepository.userIdByUsername(userName).orElseThrow(
                () -> new UserNotFoundException("User not found with username " + userName)
        );


        FollowRequestEntity followRequest = followRequestRepository.findById(reqID).orElseThrow(
                () -> new RuntimeException("Follow request not found")
        );

        if (!followRequest.getToUser().getId().equals(currentUserId)) {
            throw new RuntimeException("You are not authorized to accept this follow request");
        }

        FollowEntity followEntity = FollowEntity.builder()
                .follower(followRequest.getFromUser())
                .followee(followRequest.getToUser())
                .createdAt(Instant.now())
                .build();

        followRequestRepository.deleteById(reqID);
        followRepository.save(followEntity);

        return null;
    }

    @Transactional
    public Void rejectFollowRequest(Long reqID, Authentication authentication) {

        String userName = authentication.getName();
        Long currentUserId = userRepository.userIdByUsername(userName).orElseThrow(
                () -> new UserNotFoundException("User not found with username " + userName)
        );

        FollowRequestEntity request = followRequestRepository.findById(reqID).orElseThrow(
                () -> new RuntimeException("Follow request not found")
        );

        if (!request.getToUser().getId().equals(currentUserId)) {
            throw new RuntimeException("You are not authorized to reject this follow request");
        }

        followRequestRepository.deleteById(reqID);
        return null;
    }

    @Transactional
    public Void removeFollower(
            @NotNull @Min(1) Long followerId,
            Authentication authentication
    ) {
        String name = authentication.getName();

        UserEntity currUser = userRepository.findByUsername(name)
                .orElseThrow(() -> new UserNotFoundException("User not found with username " + name));

        FollowEntity followEntity = followRepository.findByFollowerIdAndFolloweeId(followerId, currUser.getId())
                .orElseThrow(() -> new RuntimeException("You are not followed by this user"));

        followRepository.delete(followEntity);

        UserEntity follower = followEntity.getFollower();

        Long myFollowerCount = currUser.getFollowersCount();
        currUser.setFollowersCount(myFollowerCount - 1);

        Long hisFollowingCount = follower.getFollowingCount();
        follower.setFollowingCount(hisFollowingCount - 1);
        follower.setFollowingCount(hisFollowingCount - 1);

        return null;
    }

    @Transactional
    public Void blockUser(@NotNull @Min(1) Long userId, Authentication authentication) {

        String name = authentication.getName();
        UserEntity currentUser = userRepository.findByUsername(name)
                .orElseThrow(() -> new UserNotFoundException("User not found with name " + name));

        if (currentUser.getId().equals(userId)) {
            throw new RuntimeException("You cannot block yourself");
        }

        UserEntity userToBlock = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        if (blockRepository.findByBlockerAndBlockedUser(currentUser.getId(), userId).isPresent()) {
            throw new RuntimeException("You have already blocked this user");
        }

        blockRepository.save(
                BlockEntity.builder()
                        .blocker(currentUser)
                        .blockedUser(userToBlock)
                        .blockedAt(Instant.now())
                        .build()
        );

        // Remove follow relationships if they exist
        followRepository.findByFollowerIdAndFolloweeId(currentUser.getId(), userId)
                .ifPresent(followRepository::delete);


        // Remove me from his followers if he follows me
        Optional<FollowEntity> blockedUserFollows = followRepository.findByFollowerIdAndFolloweeId(userToBlock.getId(), currentUser.getId());

        if (blockedUserFollows.isPresent()) {
            followRepository.delete(blockedUserFollows.get());

            UserEntity follower = blockedUserFollows.get().getFollower();

            Long myFollowerCount = currentUser.getFollowersCount();
            currentUser.setFollowersCount(myFollowerCount - 1);

            Long hisFollowingCount = follower.getFollowingCount();
            follower.setFollowingCount(hisFollowingCount - 1);
        }


        // Remove follower
        Optional<FollowEntity> iFollowBlockedUser = followRepository.findByFollowerIdAndFolloweeId(currentUser.getId(), userToBlock.getId());

        if (iFollowBlockedUser.isPresent()) {
            followRepository.delete(iFollowBlockedUser.get());

            Long myFollowingCount = currentUser.getFollowingCount();
            currentUser.setFollowingCount(myFollowingCount - 1);

            Long hisFollowerCount = userToBlock.getFollowersCount();
            userToBlock.setFollowersCount(hisFollowerCount - 1);
        }

        followRequestRepository.findByFromUserIdAndToUserId(currentUser.getId(), userId)
                .ifPresent(followRequestRepository::delete);

        followRequestRepository.findByFromUserIdAndToUserId(userId, currentUser.getId())
                .ifPresent(followRequestRepository::delete);

        return null;
    }

    public Void unblockUser(@NotNull @Min(1) Long userId, Authentication authentication) {
        String name = authentication.getName();
        Long currentUserId = userRepository.userIdByUsername(name)
                .orElseThrow(() -> new UserNotFoundException("User not found with name " + name));

        BlockEntity blockEntity = (BlockEntity) blockRepository.findByBlockerAndBlockedUser(currentUserId, userId)
                .orElseThrow(() -> new RuntimeException("You have not blocked this user yet, do you wanna block him/her first ? :D"));

        blockRepository.delete(blockEntity);

        return null;
    }
}

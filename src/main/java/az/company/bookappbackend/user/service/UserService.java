package az.company.bookappbackend.user.service;

import az.company.bookappbackend.minio.service.MinioStorageService;
import az.company.bookappbackend.user.UserMapper;
import az.company.bookappbackend.user.dto.request.EditUserInfoRequest;
import az.company.bookappbackend.user.dto.response.SimpleUserProfileDto;
import az.company.bookappbackend.user.dto.response.UpdatedUserProfileDto;
import az.company.bookappbackend.user.dto.response.UserAvatarResponse;
import az.company.bookappbackend.user.dto.response.UserProfileResponse;
import az.company.bookappbackend.user.entity.UserEntity;
import az.company.bookappbackend.user.exceptions.UserAvatarAlreadyEmptyException;
import az.company.bookappbackend.user.exceptions.UserNotFoundException;
import az.company.bookappbackend.user.exceptions.UsernameAlreadyExists;
import az.company.bookappbackend.user.repository.UserRepository;
import io.minio.GetObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MinioStorageService minioStorageService;
    private final UserMapper userMapper;

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
}

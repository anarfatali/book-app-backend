package az.company.bookappbackend.user.service;

import az.company.bookappbackend.storage_service.FileContent;
import az.company.bookappbackend.storage_service.FileUtility;
import az.company.bookappbackend.storage_service.StorageService;
import az.company.bookappbackend.user.dto.request.EditUserInfoRequest;
import az.company.bookappbackend.user.dto.response.SimpleUserProfileDto;
import az.company.bookappbackend.user.dto.response.UpdatedUserProfileDto;
import az.company.bookappbackend.user.dto.response.UserProfileResponse;
import az.company.bookappbackend.user.entity.UserEntity;
import az.company.bookappbackend.user.exceptions.FileNotFoundException;
import az.company.bookappbackend.user.exceptions.UserAvatarAlreadyEmptyException;
import az.company.bookappbackend.user.exceptions.UserAvatarIsEmptyException;
import az.company.bookappbackend.user.exceptions.UserNotFoundException;
import az.company.bookappbackend.user.exceptions.UsernameAlreadyExists;
import az.company.bookappbackend.user.mapper.UserMapper;
import az.company.bookappbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static az.company.bookappbackend.storage_service.StorageConstants.PROFILE_PICTURE_BUCKET;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StorageService storageService;
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
            storageService.deleteFile(userEntity.getAvatarUrl(), PROFILE_PICTURE_BUCKET);
        }

        String fileExtension = FileUtility.getFileExtensionSafe(file.getOriginalFilename());

        String fileName = "profile_%s.%s".formatted(userEntity.getUsername(), fileExtension);

        storageService.uploadFile(fileName, PROFILE_PICTURE_BUCKET, file);

        userEntity.setAvatarUrl(fileName);

        userRepository.save(userEntity);

        return fileName;
    }

    public FileContent getUserAvatar(Long userId) {
        String avatarUrl = userRepository.findAvatarUrlByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        if (avatarUrl == null) {
            throw new UserAvatarIsEmptyException("UserAvatar is empty"); //this needs to return status code 404
        }

        return storageService.findFile(avatarUrl, PROFILE_PICTURE_BUCKET)
                .orElseThrow(() -> new FileNotFoundException("Profile picture not found with file name " + avatarUrl));
    }

    @Transactional
    public void deleteUserAvatar(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));

        String avatarUrl = userEntity.getAvatarUrl();

        if (avatarUrl == null) {
            throw new UserAvatarAlreadyEmptyException("User avatar is already empty with id " + userId);
        }

        storageService.deleteFile(avatarUrl, PROFILE_PICTURE_BUCKET);

        userEntity.setAvatarUrl(null);
        userRepository.save(userEntity);
    }
}

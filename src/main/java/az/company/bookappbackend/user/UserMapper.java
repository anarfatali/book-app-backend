package az.company.bookappbackend.user;

import az.company.bookappbackend.user.dto.request.EditUserInfoRequest;
import az.company.bookappbackend.user.dto.response.SimpleUserProfileDto;
import az.company.bookappbackend.user.dto.response.UpdatedUserProfileDto;
import az.company.bookappbackend.user.dto.response.UserProfileResponse;
import az.company.bookappbackend.user.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserProfileResponse toUserProfileResponse(
            UserEntity userEntity,
            Long followersCount,
            Long followingCount,
            Long activitiesCount
    ) {
        return new UserProfileResponse(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getBio(),
                followersCount,
                followingCount,
                activitiesCount,
                userEntity.getSubscriptionType(),
                userEntity.isVerified()
        );
    }

    public SimpleUserProfileDto toSimpleUserProfileDto(UserEntity userEntity) {
        return new SimpleUserProfileDto(userEntity.getId(), userEntity.getUsername());
    }

    public UpdatedUserProfileDto toUpdatedUserProfileDto(UserEntity userEntity) {
        return new UpdatedUserProfileDto(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getBio(),
                userEntity.getReadingExperience(),
                userEntity.getInterests()
        );
    }

    public UserEntity updateUserEntity(UserEntity userEntity, EditUserInfoRequest request) {

        if (request.username() != null) {
            userEntity.setUsername(request.username());
        }
        if (request.bio() != null) {
            userEntity.setBio(request.bio());
        }
        if (request.readingExperience() != null) {
            userEntity.setReadingExperience(request.readingExperience());
        }
        if (request.interests() != null) {
            userEntity.replaceInterests(request.interests());
        }

        return userEntity;
    }
}

package az.company.bookappbackend.user.mapper;

import az.company.bookappbackend.user.dto.UserProfileDto;
import az.company.bookappbackend.user.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserProfileDto toUserDto(UserEntity user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .name(user.getName())
                .surname(user.getSurname())
                .birthday(user.getBirthday())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .readingExperience(user.getReadingExperience())
                .interests(user.getInterests())
                .subscriptionType(user.getSubscriptionType())
                .readingFrequency(user.getReadingFrequency())
                .isPrivate(user.isPrivate())
                .verified(user.isVerified())
                .build();
    }
}

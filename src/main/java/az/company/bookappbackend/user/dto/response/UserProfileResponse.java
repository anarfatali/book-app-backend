package az.company.bookappbackend.user.dto.response;

import az.company.bookappbackend.common.enums.SubscriptionType;

public record UserProfileResponse(
        Long id,
        String username,
        String bio,
        Long followersCount,
        Long followingCount,
        Long activitiesCount,
        SubscriptionType subscriptionType,
        boolean isVerified
) {
}

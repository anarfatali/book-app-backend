package az.company.bookappbackend.user.dto.response;

import az.company.bookappbackend.common.enums.Interest;

import java.util.Set;

public record UpdatedUserProfileDto(
        Long id,
        String username,
        String bio,
        String readingExperience,
        Set<Interest> interests
) {
}

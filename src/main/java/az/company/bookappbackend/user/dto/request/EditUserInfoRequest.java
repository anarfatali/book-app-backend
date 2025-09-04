package az.company.bookappbackend.user.dto.request;

import az.company.bookappbackend.common.enums.Interest;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record EditUserInfoRequest(

        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @Size(max = 250, message = "Bio cannot exceed 250 characters")
        String bio,

        @Size(max = 100, message = "Reading experience cannot exceed 100 characters")
        String readingExperience,

        Set<Interest> interests
) {
}

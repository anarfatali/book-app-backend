package az.company.bookappbackend.auth.dto.request;

import az.company.bookappbackend.common.enums.Interests;
import az.company.bookappbackend.common.enums.ReadingFrequency;
import az.company.bookappbackend.common.enums.SubscriptionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequest
        (
                @Email(message = "Invalid email format")
                @NotBlank(message = "Email is required")
                String email,

                @NotBlank(message = "Password is required")
                @Size(min = 8, message = "Password must be at least 8 characters")
                @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
                        message = "Password must contain at least one uppercase letter, one lowercase letter, and one number")
                String password,

                @NotBlank(message = "Username is required")
                @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
                @Pattern(regexp = "^\\w+$", message = "Username can only contain letters, numbers, and underscores")
                String username,

                @NotBlank(message = "Name is required")
                @Size(max = 100, message = "Name cannot exceed 100 characters")
                String name,

                @NotBlank(message = "Surname is required")
                @Size(max = 100, message = "Surname cannot exceed 100 characters")
                String surname,

                @NotNull(message = "Birthday is required")
                @Past(message = "Birthday must be in the past")
                LocalDate birthday,

                @NotNull(message = "Subscription type is required")
                SubscriptionType subscriptionType,

                @NotNull(message = "Reading frequency is required")
                ReadingFrequency readingFrequency,

                @NotNull(message = "Interests are required")
                Interests interests,

                @Size(max = 500, message = "Bio cannot exceed 500 characters")
                String bio,

                @NotNull(message = "Reading experience is required")
                @Size(max = 100, message = "Reading experience cannot exceed 100 characters")
                String readingExperience,

                boolean isPrivate
        ) {
}
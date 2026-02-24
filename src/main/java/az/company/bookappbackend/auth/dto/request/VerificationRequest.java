package az.company.bookappbackend.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerificationRequest
        (
                @NotBlank(message = "Email is required")
                @Email(message = "Invalid email format")
                String email,

                @NotBlank(message = "OTP is required")
                @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits")
                String otp
        ) {
}

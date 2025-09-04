package az.company.bookappbackend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest
        (
                @NotBlank(message = "Email or username is required")
                String identifier,

                @NotBlank(message = "Password is required")
                @Size(min = 8, message = "Password must be at least 8 characters")
                @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
                        message = "Password must contain at least one uppercase letter, one lowercase letter, and one number")
                String password
        ) {
}

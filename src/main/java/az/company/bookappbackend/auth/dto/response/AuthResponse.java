package az.company.bookappbackend.auth.dto.response;

import az.company.bookappbackend.user.dto.response.UserProfileDto;

public record AuthResponse
        (
                String message,

                String accessToken,

                String refreshToken,

                String tokenType,

                Long expiresIn,

                UserProfileDto user
        ) {
}

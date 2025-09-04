package az.company.bookappbackend.user.dto.response;

import io.minio.GetObjectResponse;

public record UserAvatarResponse(
        String avatarUrl,
        String contentType,
        GetObjectResponse file
) {
}

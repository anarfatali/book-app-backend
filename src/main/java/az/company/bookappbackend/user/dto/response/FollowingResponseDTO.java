package az.company.bookappbackend.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowingResponseDTO {
    private Long id;
    private String username;
    private boolean isFollowing;
    private boolean isFollowedBy;
}

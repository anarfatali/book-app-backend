package az.company.bookappbackend.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalErrorResponse implements Serializable {

    private UUID requestId;
    private int errorCode;
    private String errorMessage;
    private LocalDateTime timestamp;
}

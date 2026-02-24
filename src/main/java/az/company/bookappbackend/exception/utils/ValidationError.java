package az.company.bookappbackend.exception.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ValidationError {
    private String field;
    private String rejectedValue;
    private String rejectedMessage;
}

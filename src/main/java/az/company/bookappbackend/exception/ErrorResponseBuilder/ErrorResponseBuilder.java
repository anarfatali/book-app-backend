package az.company.bookappbackend.exception.ErrorResponseBuilder;

import az.company.bookappbackend.exception.utils.ExceptionKeyAndMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;

public interface ErrorResponseBuilder {

    ResponseEntity<Object> buildHttpResponse(
            WebRequest request,
            HttpStatus httpStatus,
            String techMessage,
            int code,
            String businessMessage,
            Map<String, ?> validationErrors
    );

    ResponseEntity<Object> buildHttpResponse(
            WebRequest request,
            HttpStatus httpStatus,
            Exception ex,
            Enum<? extends ExceptionKeyAndMessage> globalExceptionCodes
    );

    ResponseEntity<Object> buildHttpResponse(
            WebRequest request,
            HttpStatus httpStatus,
            String techMessage,
            int code,
            String businessMessage,
            List<?> validationErrors
    );

    ResponseEntity<Object> buildHttpResponse(
            WebRequest request,
            HttpStatus httpStatus,
            String techMessage,
            int code,
            List<?> errors
    );
}

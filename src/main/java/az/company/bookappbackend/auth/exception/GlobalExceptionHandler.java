package az.company.bookappbackend.auth.exception;

import az.company.bookappbackend.auth.dto.response.GlobalErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<GlobalErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        addErrorLog(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex.getClass().getSimpleName());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AccountDeactivatedException.class)
    public ResponseEntity<GlobalErrorResponse> handleAccountDeactivated(AccountDeactivatedException ex) {
        addErrorLog(HttpStatus.FORBIDDEN, ex.getMessage(), ex.getClass().getSimpleName());
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<GlobalErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        addErrorLog(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getClass().getSimpleName());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<GlobalErrorResponse> handleUserNotVerified(UserNotVerifiedException ex) {
        addErrorLog(HttpStatus.FORBIDDEN, ex.getMessage(), ex.getClass().getSimpleName());
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<GlobalErrorResponse> handleAlreadyExists(AlreadyExistsException ex) {
        addErrorLog(HttpStatus.CONFLICT, ex.getMessage(), ex.getClass().getSimpleName());
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<GlobalErrorResponse> handleEmailSend(EmailSendException ex) {
        addErrorLog(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex.getClass().getSimpleName());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<GlobalErrorResponse> handleInvalidOtp(InvalidOtpException ex) {
        addErrorLog(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getClass().getSimpleName());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<GlobalErrorResponse> handleOtpExpired(OtpExpiredException ex) {
        addErrorLog(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getClass().getSimpleName());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<GlobalErrorResponse> handleInvalidToken(InvalidTokenException ex) {
        addErrorLog(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex.getClass().getSimpleName());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<GlobalErrorResponse> handleTokenExpired(TokenExpiredException ex) {
        addErrorLog(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex.getClass().getSimpleName());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<GlobalErrorResponse> handleTooManyRequests(TooManyRequestsException ex) {
        addErrorLog(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), ex.getClass().getSimpleName());
        return buildErrorResponse(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
    }

    @ExceptionHandler(VerificationNotFoundException.class)
    public ResponseEntity<GlobalErrorResponse> handleVerificationNotFound(VerificationNotFoundException ex) {
        addErrorLog(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getClass().getSimpleName());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<GlobalErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        addErrorLog(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getClass().getSimpleName());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        addErrorLog(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getClass().getSimpleName());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<GlobalErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {
        addErrorLog(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getClass().getSimpleName());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GlobalErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        addErrorLog(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex.getClass().getSimpleName());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    protected void addErrorLog(HttpStatus httpStatus, String errorMessage, String exceptionType) {
        int statusCode = (httpStatus != null) ? httpStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR.value();
        log.error("HTTP Status: {} | Error Message: {} | Exception Type: {}", statusCode, errorMessage, exceptionType);
    }

    protected ResponseEntity<GlobalErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(GlobalErrorResponse.builder()
                        .errorCode(status.value())
                        .errorMessage(message)
                        .timestamp(LocalDateTime.now())
                        .requestId(UUID.randomUUID())
                        .build());
    }
}

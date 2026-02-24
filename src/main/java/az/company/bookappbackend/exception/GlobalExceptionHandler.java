package az.company.bookappbackend.exception;

import az.company.bookappbackend.exception.ErrorResponseBuilder.ErrorResponseBuilder;
import az.company.bookappbackend.exception.utils.TechExceptionCodes;
import az.company.bookappbackend.exception.utils.ValidationError;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.ConnectException;
import java.net.SocketException;
import java.nio.file.AccessDeniedException;
import java.security.KeyStoreException;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
//@Profile("!debug")
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ErrorResponseBuilder errorResponseBuilder;

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Object> handle(
            GeneralException ex,
            WebRequest request
    ) {
        return errorResponseBuilder.buildHttpResponse(
                request,
                ex.getHttpStatus(),
                ex.getMessage(),
                ex.getCode(),
                ex.getMessage(),
                Collections.emptyList()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handle(
            RuntimeException ex,
            WebRequest request
    ) {
        log.error(
                "RuntimeException: {}",
                ex.getMessage()
        );

        // Wanna see the full stack trace in logs
        throw ex;
//todo: This is legacy code, should be refactored
//        return ofType(
//                request,
//                HttpStatus.BAD_REQUEST,
//                TechExceptionCodes.RUNTIME_EXCEPTION.getExceptionMessage(),
//                TechExceptionCodes.RUNTIME_EXCEPTION.getExceptionCode(),
//                Collections.emptyList()
//        );
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handle(
            AccessDeniedException ex,
            WebRequest request
    ) {
        log.error(
                "Access denied: {}",
                ex.getMessage()
        );
        return errorResponseBuilder.buildHttpResponse(
                request,
                HttpStatus.BAD_REQUEST,
                ex,
                TechExceptionCodes.ACCESS_DENIED
        );
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<Object> handle(
            ConnectException ex,
            WebRequest request
    ) {
        log.error(
                "Connect Exception: {}",
                ex.getMessage()
        );
        return errorResponseBuilder.buildHttpResponse(
                request,
                HttpStatus.BAD_REQUEST,
                ex,
                TechExceptionCodes.CONNECT_EXCEPTION
        );
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handle(
            MethodArgumentTypeMismatchException ex,
            WebRequest request
    ) {
        return errorResponseBuilder.buildHttpResponse(
                request,
                HttpStatus.BAD_REQUEST,
                ex,
                TechExceptionCodes.METHOD_ARGUMENT_TYPE_MISMATCH
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handle(
            NoSuchElementException ex,
            WebRequest request
    ) {
        log.error(
                "NoSuchElementException: {}",
                ex.getMessage()
        );
        return errorResponseBuilder.buildHttpResponse(
                request,
                HttpStatus.BAD_REQUEST,
                ex,
                TechExceptionCodes.NO_SUCH_ELEMENT
        );
    }

    @ExceptionHandler(KeyStoreException.class)
    public ResponseEntity<Object> handle(
            KeyStoreException ex,
            WebRequest request
    ) {
        log.error(
                "KeyStoreException: {}",
                ex.getMessage()
        );
        return errorResponseBuilder.buildHttpResponse(
                request,
                HttpStatus.BAD_REQUEST,
                ex,
                TechExceptionCodes.KEY_STORE_EXCEPTION
        );
    }

    @ExceptionHandler(SocketException.class)
    public ResponseEntity<Object> handle(
            SocketException ex,
            WebRequest request
    ) {
        log.error(
                "SocketException: {}",
                ex.getMessage()
        );
        return errorResponseBuilder.buildHttpResponse(
                request,
                HttpStatus.BAD_REQUEST,
                ex,
                TechExceptionCodes.SOCKET_EXCEPTION
        );
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Object> handle(
            DateTimeParseException ex,
            WebRequest request
    ) {
        log.error(
                "DateTimeParseException: {}",
                ex.getMessage()
        );
        return errorResponseBuilder.buildHttpResponse(
                request,
                HttpStatus.BAD_REQUEST,
                ex,
                TechExceptionCodes.DATE_TIME_PARSE
        );
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Object> handle(
            InvalidFormatException ex,
            WebRequest request
    ) {
        log.error(
                "InvalidFormatException: {}",
                ex.getMessage()
        );
        return errorResponseBuilder.buildHttpResponse(
                request,
                HttpStatus.BAD_REQUEST,
                ex,
                TechExceptionCodes.INVALID_FORMAT
        );
    }

    @ExceptionHandler(NoSuchBeanDefinitionException.class)
    public ResponseEntity<Object> handle(
            NoSuchBeanDefinitionException ex,
            WebRequest request
    ) {
        log.error(
                "NoSuchBeanDefinitionException: {}",
                ex.getMessage()
        );
        return errorResponseBuilder.buildHttpResponse(
                request,
                HttpStatus.BAD_REQUEST,
                ex,
                TechExceptionCodes.NO_SUCH_BEAN_DEFINITION
        );
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> handle(
            NullPointerException ex,
            WebRequest request
    ) {
        log.error(
                "NullPointerException: {}",
                ex.getMessage()
        );
        return errorResponseBuilder.buildHttpResponse(
                request,
                HttpStatus.BAD_REQUEST,
                ex,
                TechExceptionCodes.NULL_POINTER
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handle(
            IllegalArgumentException ex,
            WebRequest request
    ) {
        log.error(
                "IllegalArgumentException: {}",
                ex.getMessage()
        );
        return errorResponseBuilder.buildHttpResponse(
                request,
                HttpStatus.BAD_REQUEST,
                ex,
                TechExceptionCodes.ILLEGAL_ARGUMENT
        );
    }

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<Object> handle(
            ConversionFailedException ex,
            WebRequest request
    ) {
        log.error(
                "ConversionFailedException: {}",
                ex.getMessage()
        );
        return errorResponseBuilder.buildHttpResponse(
                request,
                HttpStatus.BAD_REQUEST,
                ex,
                TechExceptionCodes.CONVERSION_EXCEPTION
        );
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public ResponseEntity<Object> handle(
            UnexpectedTypeException ex,
            WebRequest request
    ) {
        log.error(
                "UnexpectedTypeException: {}",
                ex.getMessage()
        );
        return errorResponseBuilder.buildHttpResponse(
                request,
                HttpStatus.BAD_REQUEST,
                ex,
                TechExceptionCodes.UNEXPECTED_TYPE
        );
    }

    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<Object> handle(
            ArithmeticException ex,
            WebRequest request
    ) {
        log.error(
                "ArithmeticException: {}",
                ex.getMessage()
        );
        return errorResponseBuilder.buildHttpResponse(
                request,
                HttpStatus.BAD_REQUEST,
                ex,
                TechExceptionCodes.ARITHMETIC
        );
    }

    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<Object> handle(
            ClassCastException ex,
            WebRequest request
    ) {
        log.error(
                "ClassCastException: {}",
                ex.getMessage()
        );
        return errorResponseBuilder.buildHttpResponse(
                request,
                HttpStatus.BAD_REQUEST,
                ex,
                TechExceptionCodes.CLASS_CAST_EXCEPTION
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handle(
            ConstraintViolationException ex,
            WebRequest request
    ) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        List<ValidationError> validationErrors = constraintViolations
                .stream()
                .map(p -> ValidationError
                        .builder()
                        .field(p
                                       .getPropertyPath()
                                       .toString()
                        )
                        .rejectedValue(Objects
                                               .requireNonNull(p.getInvalidValue())
                                               .toString()
                        )
                        .rejectedMessage(p.getMessage())
                        .build())
                .collect(Collectors.toList())
                ;
        log.error(
                "javax.validation.ConstraintViolationException: {}",
                ex.getMessage()
        );
        return errorResponseBuilder.buildHttpResponse(
                request,
                HttpStatus.BAD_REQUEST,
                TechExceptionCodes.CONSTRAINT_VIOLATION.getExceptionMessage(),
                TechExceptionCodes.CONSTRAINT_VIOLATION.getExceptionCode(),
                validationErrors
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {

        return errorResponseBuilder.buildHttpResponse(
                request,
                HttpStatus.BAD_REQUEST,
                TechExceptionCodes.METHOD_ARGUMENT_NOT_VALID.getExceptionMessage(),
                TechExceptionCodes.METHOD_ARGUMENT_NOT_VALID.getExceptionCode(),
                TechExceptionCodes.METHOD_ARGUMENT_NOT_VALID.getExceptionMessage(),

                ex
                        .getFieldErrors()
                        .stream()
                        .map(
                                fieldError -> new FieldError(
                                        fieldError.getField(),
                                        fieldError.getDefaultMessage(),
                                        fieldError.getRejectedValue()
                                )
                        )
                        .collect(Collectors.toList())
        );
    }

    @Data
    @AllArgsConstructor
    static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}

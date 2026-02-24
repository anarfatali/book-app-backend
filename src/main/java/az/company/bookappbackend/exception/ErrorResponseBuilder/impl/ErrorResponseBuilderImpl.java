package az.company.bookappbackend.exception.ErrorResponseBuilder.impl;

import az.company.bookappbackend.exception.ErrorResponseBuilder.ErrorResponseBuilder;
import az.company.bookappbackend.exception.utils.ExceptionKeyAndMessage;
import az.company.bookappbackend.exception.utils.GeneralExceptionMessages;
import az.company.bookappbackend.exception.utils.HeaderAttributeKeys;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ErrorResponseBuilderImpl implements ErrorResponseBuilder {

    @Override
    public ResponseEntity<Object> buildHttpResponse(
            WebRequest request,
            HttpStatus httpStatus,
            String techMessage,
            int code,
            List<?> errors
    ) {

        String exceptionMessage = GeneralExceptionMessages.getExceptionByCode("AZ");

        return buildHttpResponse(
                request,
                httpStatus,
                techMessage,
                code,
                exceptionMessage,
                errors
        );
    }

    @Override
    public ResponseEntity<Object> buildHttpResponse(
            WebRequest request,
            HttpStatus httpStatus,
            String techMessage,
            int code,
            String businessMessage,
            List<?> validationErrors
    ) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(
                HeaderAttributeKeys.CODE,
                code
        );
        attributes.put(
                HeaderAttributeKeys.MESSAGE,
                businessMessage.replace(
                        "\\n",
                        "\n"
                )
        );
        if (Objects.nonNull(techMessage)) {
            attributes.put(
                    HeaderAttributeKeys.TECH_MESSAGE,
                    techMessage
            );
        }
        attributes.put(
                HeaderAttributeKeys.STATUS,
                httpStatus.value()
        );
        attributes.put(
                HeaderAttributeKeys.ERROR,
                httpStatus.getReasonPhrase()
        );
        if (!validationErrors.isEmpty()) {
            attributes.put(
                    HeaderAttributeKeys.VALIDATION_ERRORS,
                    validationErrors
            );
        }
        attributes.put(
                HeaderAttributeKeys.TIMESTAMP,
                LocalDateTime
                        .now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
        attributes.put(
                HeaderAttributeKeys.PATH,
                ((ServletWebRequest) request)
                        .getRequest()
                        .getRequestURI()
        );
        return new ResponseEntity<>(
                attributes,
                httpStatus
        );
    }


    @Override
    public ResponseEntity<Object> buildHttpResponse(
            WebRequest request,
            HttpStatus httpStatus,
            String techMessage,
            int code,
            String businessMessage,
            Map<String, ?> validationErrors
    ) {

        Map<String, Object> attributes = new HashMap<>();

        attributes.put(
                HeaderAttributeKeys.CODE,
                code
        );
        attributes.put(
                HeaderAttributeKeys.MESSAGE,
                businessMessage.replace(
                        "\\n",
                        "\n"
                )
        );
        if (Objects.nonNull(techMessage)) {
            attributes.put(
                    HeaderAttributeKeys.TECH_MESSAGE,
                    techMessage
            );
        }
        attributes.put(
                HeaderAttributeKeys.STATUS,
                httpStatus.value()
        );
        attributes.put(
                HeaderAttributeKeys.ERROR,
                httpStatus.getReasonPhrase()
        );
        if (!validationErrors.isEmpty()) {
            attributes.put(
                    HeaderAttributeKeys.VALIDATION_ERRORS,
                    validationErrors
            );
        }
        attributes.put(
                HeaderAttributeKeys.TIMESTAMP,
                LocalDateTime
                        .now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
        attributes.put(
                HeaderAttributeKeys.PATH,
                ((ServletWebRequest) request)
                        .getRequest()
                        .getRequestURI()
        );

        return new ResponseEntity<>(
                attributes,
                httpStatus
        );
    }

    @Override
    public ResponseEntity<Object> buildHttpResponse(
            WebRequest request,
            HttpStatus httpStatus,
            Exception ex,
            Enum<? extends ExceptionKeyAndMessage> globalExceptionCodes
    ) {
        return buildHttpResponse(
                request,
                httpStatus,
                ((ExceptionKeyAndMessage) globalExceptionCodes).getExceptionMessage() + "(" + ex.getMessage() + ")",
                ((ExceptionKeyAndMessage) globalExceptionCodes).getExceptionCode(),
                Collections.emptyList()
        );
    }
}

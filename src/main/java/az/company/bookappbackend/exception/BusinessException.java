package az.company.bookappbackend.exception;

import az.company.bookappbackend.exception.enums.BusinessStatus;
import org.springframework.http.HttpStatus;

public class BusinessException extends GeneralException {

    public BusinessException(
            BusinessStatus businessExceptionCodes,
            HttpStatus httpStatus
    ) {
        super(
                businessExceptionCodes,
                httpStatus
        );
    }

    public BusinessException(
            String message,
            BusinessStatus businessExceptionCodes,
            HttpStatus httpStatus
    ) {
        super(
                message,
                businessExceptionCodes,
                httpStatus
        );
    }

    public BusinessException(
            Throwable cause,
            BusinessStatus businessExceptionCodes,
            HttpStatus httpStatus
    ) {
        super(
                cause,
                businessExceptionCodes,
                httpStatus
        );
    }
}

package az.company.bookappbackend.exception;

import az.company.bookappbackend.exception.utils.TechExceptionCodes;
import org.springframework.http.HttpStatus;

public class TechException extends GeneralException {

    public TechException(
            TechExceptionCodes techExceptionCodes,
            HttpStatus httpStatus
    ) {
        super(
                techExceptionCodes,
                httpStatus
        );
    }

    public TechException(
            String message,
            TechExceptionCodes techExceptionCodes,
            HttpStatus httpStatus
    ) {
        super(
                message,
                techExceptionCodes,
                httpStatus
        );
    }

    public TechException(
            Throwable cause,
            TechExceptionCodes techExceptionCodes,
            HttpStatus httpStatus
    ) {
        super(
                cause,
                techExceptionCodes,
                httpStatus
        );
    }
}


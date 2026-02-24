package az.company.bookappbackend.exception;

import az.company.bookappbackend.exception.utils.ExceptionKeyAndMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GeneralException extends RuntimeException {

    private final int code;
    private final String title;
    private final HttpStatus httpStatus;

    public GeneralException(
            Enum<? extends ExceptionKeyAndMessage> keyAndMessage,
            HttpStatus httpStatus
    ) {
        super(((ExceptionKeyAndMessage) keyAndMessage).getExceptionMessage());
        ExceptionKeyAndMessage key = (ExceptionKeyAndMessage) keyAndMessage;
        this.code = key.getExceptionCode();
        this.title = key.getExceptionTitle();
        this.httpStatus = httpStatus;
    }

    public GeneralException(
            String message,
            Enum<? extends ExceptionKeyAndMessage> keyAndMessage,
            HttpStatus httpStatus
    ) {
        super(((ExceptionKeyAndMessage) keyAndMessage).getExceptionMessage() + " " + message);
        ExceptionKeyAndMessage key = (ExceptionKeyAndMessage) keyAndMessage;
        this.code = key.getExceptionCode();
        this.title = key.getExceptionTitle();
        this.httpStatus = httpStatus;
    }

    public GeneralException(
            Throwable cause,
            Enum<? extends ExceptionKeyAndMessage> keyAndMessage,
            HttpStatus httpStatus
    ) {
        super(cause);
        ExceptionKeyAndMessage key = (ExceptionKeyAndMessage) keyAndMessage;
        this.code = key.getExceptionCode();
        this.title = key.getExceptionTitle();
        this.httpStatus = httpStatus;
    }

}

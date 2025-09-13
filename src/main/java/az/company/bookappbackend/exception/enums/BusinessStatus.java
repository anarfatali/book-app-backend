package az.company.bookappbackend.exception.enums;


import az.company.bookappbackend.exception.utils.ExceptionKeyAndMessage;
import az.company.bookappbackend.exception.utils.HeaderAttributeKeys;

public enum BusinessStatus implements ExceptionKeyAndMessage {
    // 0 - 999 - Common
    COMMON_SUCCESS(
            0,
            HeaderAttributeKeys.STATUS,
            "Successfully process!"
    ),
    INVALID_ARGUMENTS(
            1,
            HeaderAttributeKeys.VALIDATION_ERRORS,
            "Invalid parameters"
    ),
    UNKNOWN_ERROR(
            2,
            HeaderAttributeKeys.ERROR,
            "Internal Server Error"
    ),
    DATA_NOT_FOUND(
            3,
            HeaderAttributeKeys.ERROR,
            "Data not found"
    ),

    // 1000 - 1999 - User

    // 2000 - 2999 - Book

    ;


    private final int code;
    private final String title;
    private final String message;

    BusinessStatus(
            int code,
            String title,
            String message
    ) {
        this.code = code;
        this.title = title;
        this.message = message;
    }

    @Override
    public int getExceptionCode() {
        return code;
    }

    @Override
    public String getExceptionTitle() {
        return title;
    }

    @Override
    public String getExceptionMessage() {
        return message;
    }
}

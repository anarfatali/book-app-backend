package az.company.bookappbackend.exception.utils;

public enum TechExceptionCodes implements ExceptionKeyAndMessage {
    NO_SUCH_ELEMENT(
            1001,
            "No such element"
    ),
    METHOD_ARGUMENT_TYPE_MISMATCH(
            1002,
            "Method argument type mismatch"
    ),
    REQUEST_METHOD_NOT_SUPPORTED(
            1003,
            "Request method not supported"
    ),
    MEDIA_TYPE_NOT_SUPPORTED(
            1004,
            "Media type not supported"
    ),
    MEDIA_TYPE_NOT_ACCEPTABLE(
            1005,
            "Media type not acceptable"
    ),
    MISSING_PATH_VARIABLE(
            1006,
            "Missing path variable"
    ),
    MISSING_SERVLET_REQUEST_PARAMETER(
            1007,
            "Missing servlet request parameter"
    ),
    SERVLET_REQUEST_BINDING(
            1008,
            "Servlet request binding"
    ),
    CONVERSION_NOT_SUPPORTED(
            1009,
            "Converion not supported"
    ),
    TYPE_MISMATCH(
            1010,
            "Type mismatch"
    ),
    MESSAGE_NOT_READABLE(
            1011,
            "Message not readable"
    ),
    MESSAGE_NOT_WRITABLE(
            1012,
            "Message not writable"
    ),
    MISSING_SERVLET_REQUEST_PART(
            1013,
            "Missing servlet request part"
    ),
    BIND_EXCEPTION(
            1014,
            "Bind Exception"
    ),
    NO_HANDLER_FOUND(
            1015,
            "No handler found"
    ),
    ASYNC_REQUEST_TIMEOUT(
            1016,
            "Async request timeout"
    ),
    INTERNAL(
            1017,
            "Internal"
    ),
    CONSTRAINT_VIOLATION(
            1018,
            "Constraint Violation"
    ),
    ARITHMETIC(
            1019,
            "Arithmetic"
    ),
    UNEXPECTED_TYPE(
            1020,
            "Unexpected type"
    ),
    ACCESS_DENIED(
            1021,
            "Access denied"
    ),
    NULL_POINTER(
            1022,
            "NullPointer"
    ),
    NO_SUCH_BEAN_DEFINITION(
            1023,
            "NO_SUCH_BEAN_DEFINITION"
    ),
    MAX_UPLOAD_SIZE_EXCEEDED(
            1024,
            "Max upload size exceeded"
    ),
    CONNECT_EXCEPTION(
            1025,
            "Give some exception when creating connection"
    ),
    METHOD_ARGUMENT_NOT_VALID(
            1026,
            "Argument validation failed"
    ),
    FEIGN_CLIENT(
            1027,
            "Feign Client Side Exception"
    ),
    FEIGN_INTERNAL_CALL_AUTHORIZATION(
            1028,
            "AUTHORIZATION need for internal client call"
    ),
    FEIGN_RESPONSE_CANT_MAP_TO_MESSAGE(
            1029,
            "Feign reponse body didn't map to certain response body."
    ),
    DATE_TIME_PARSE(
            1030,
            "Date time is not parse correctly."
    ),
    INVALID_FORMAT(
            1031,
            "Invalid format exception"
    ),
    ILLEGAL_ARGUMENT(
            1032,
            "Illegal argument"
    ),
    SOCKET_EXCEPTION(
            1033,
            "Invalid format exception"
    ),
    JSON_PARSE_EXCEPTION(
            1034,
            "Json didn't parse to object completely"
    ),
    KEY_STORE_EXCEPTION(
            1035,
            "Key store exception"
    ),
    RUNTIME_EXCEPTION(
            1036,
            "You have new exception which didn't handle it"
    ),
    CONVERSION_EXCEPTION(
            1037,
            "Cannot convert the enum value, no enum matching"
    ),
    NOT_DATA_FOUND_EXCEPTION(
            1038,
            "Data not found"
    ),
    RETRYABLE_EXCEPTION(
            1039,
            "Retryable Exception"
    ),
    USER_BLOCKED(
            1040,
            "User is blocked"
    ),
    CLASS_CAST_EXCEPTION(
            1041,
            "Request header is missing"
    ),
    INVALID_REQUEST_EXCEPTION(
            1042,
            "Request is invalid"
    );

    private final int externalCode;
    private final String description;
    private final String title;

    TechExceptionCodes(
            int externalCode,
            String description
    ) {
        this.externalCode = externalCode;
        this.description = description;
        this.title = "error";
    }

    @Override
    public int getExceptionCode() {
        return externalCode;
    }

    @Override
    public String getExceptionTitle() {
        return title;
    }

    @Override
    public String getExceptionMessage() {
        return description;
    }

}


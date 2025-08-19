package az.company.bookappbackend.auth.exception;

public class EmailSendException extends RuntimeException {

    public EmailSendException(String message) {
        super(message);
    }
}

package az.company.bookappbackend.auth.exception;

public class VerificationNotFoundException extends RuntimeException {

    public VerificationNotFoundException(String message) {
        super(message);
    }
}

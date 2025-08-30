package az.company.bookappbackend.user.exceptions;

public class UserAvatarAlreadyEmptyException extends RuntimeException {
    public UserAvatarAlreadyEmptyException(String message) {
        super(message);
    }
}

package az.company.bookappbackend.user.exceptions;

public class UserAvatarIsEmptyException extends RuntimeException {
    public UserAvatarIsEmptyException(String message) {
        super(message);
    }
}

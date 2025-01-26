package school.faang.user_service.exception;

public class UserWasNotFoundException extends RuntimeException {
    public UserWasNotFoundException(String message) {
        super(message);
    }
}

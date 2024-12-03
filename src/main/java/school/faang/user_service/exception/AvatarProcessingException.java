package school.faang.user_service.exception;

public class AvatarProcessingException extends RuntimeException {
    public AvatarProcessingException(String message) {
        super(message);
    }

    public AvatarProcessingException(String message, Throwable reason) {
        super(message, reason);
    }

}

package school.faang.user_service.exception;

public class DiceBearException extends RuntimeException {
    public DiceBearException(String message, Throwable cause) {
        super(message, cause);
    }

    public DiceBearException(String message) {
        super(message);
    }
}

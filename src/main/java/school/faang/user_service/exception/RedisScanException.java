package school.faang.user_service.exception;

public class RedisScanException extends RuntimeException {
    public RedisScanException(String message) {
        super(message);
    }
}

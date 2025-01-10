package school.faang.user_service.exception;

public class InvalidRequestStatusException extends  RuntimeException {
    public InvalidRequestStatusException(String message) {
        super(message);
    }
}

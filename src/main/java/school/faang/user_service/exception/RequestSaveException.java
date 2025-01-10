package school.faang.user_service.exception;

public class RequestSaveException extends RuntimeException{
    public RequestSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}

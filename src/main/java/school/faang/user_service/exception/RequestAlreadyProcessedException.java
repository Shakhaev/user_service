package school.faang.user_service.exception;

public class RequestAlreadyProcessedException extends RuntimeException {
    public RequestAlreadyProcessedException(String message) {
        super(message);
    }
}

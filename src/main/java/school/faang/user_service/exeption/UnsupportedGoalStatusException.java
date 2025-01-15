package school.faang.user_service.exeption;

public class UnsupportedGoalStatusException extends RuntimeException {
    public UnsupportedGoalStatusException(String message) {
        super(message);
    }
}

package school.faang.user_service.exeption;

public class InactiveGoalUpdateException extends RuntimeException {
    public InactiveGoalUpdateException(String message) {
        super(message);
    }
}

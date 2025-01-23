package school.faang.user_service.exception;

public class UserGoalLimitExceededException extends RuntimeException {
    public UserGoalLimitExceededException(String message) {
        super(message);
    }
}

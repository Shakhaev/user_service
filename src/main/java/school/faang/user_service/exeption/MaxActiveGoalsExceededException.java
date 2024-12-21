package school.faang.user_service.exeption;

public class MaxActiveGoalsExceededException extends RuntimeException {
    public MaxActiveGoalsExceededException(String message) {
        super(message);
    }
}
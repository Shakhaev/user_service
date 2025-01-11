package school.faang.user_service.exception.goal.invitation;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class UserAlreadyHasGoalException extends ApiException {
    private static final String MESSAGE = "User with id: %s already has goal with id: %s";

    public UserAlreadyHasGoalException(Long userId, Long goalId) {
        super(MESSAGE, BAD_REQUEST, userId, goalId);
    }
}

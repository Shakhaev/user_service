package school.faang.user_service.exception.goal.invitation;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class UserGoalsLimitExceededException extends ApiException {
    private static final String MESSAGE = "Number of goals in User with id: %s exceeds the allowed limit of: %s";

    public UserGoalsLimitExceededException(Long userId, Integer goalsLimit) {
        super(MESSAGE, BAD_REQUEST, userId, goalsLimit);
    }
}

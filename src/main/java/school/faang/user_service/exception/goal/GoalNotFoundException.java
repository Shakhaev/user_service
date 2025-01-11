package school.faang.user_service.exception.goal;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class GoalNotFoundException extends ApiException {
    private static final String MESSAGE = "Goal with id: %s not found";

    public GoalNotFoundException(Long goalId) {
        super(MESSAGE, NOT_FOUND, goalId);
    }
}

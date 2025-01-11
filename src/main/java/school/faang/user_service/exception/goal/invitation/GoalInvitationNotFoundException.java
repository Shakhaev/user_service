package school.faang.user_service.exception.goal.invitation;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class GoalInvitationNotFoundException extends ApiException {
    private static final String MESSAGE = "Goal invitation with id: %s not found";

    public GoalInvitationNotFoundException(Long id) {
        super(MESSAGE, NOT_FOUND, id);
    }
}

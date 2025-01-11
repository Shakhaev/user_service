package school.faang.user_service.exception.goal.invitation;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class SameInviterAndInvitedUsersException extends ApiException {
    private static final String MESSAGE = "Inviter user id: %s and invited user id: %s should not be same";

    public SameInviterAndInvitedUsersException(Long inviterId, Long invitedId) {
        super(MESSAGE, BAD_REQUEST, invitedId, inviterId);
    }
}

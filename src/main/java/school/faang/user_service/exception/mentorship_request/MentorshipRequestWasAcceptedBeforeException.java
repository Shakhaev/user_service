package school.faang.user_service.exception.mentorship_request;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class MentorshipRequestWasAcceptedBeforeException extends ApiException {
    private static final String MESSAGE = "Mentorship request from user with id %d to user with %d was accepted before";

    public MentorshipRequestWasAcceptedBeforeException(Long fromUserId, Long toUserId) {
        super(MESSAGE, BAD_REQUEST, fromUserId, toUserId);
    }
}

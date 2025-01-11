package school.faang.user_service.exception.mentorship_request;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class MentorshipRequestNotFoundException extends ApiException {
    private static final String MESSAGE = "Mentorship request with id %d not found";

    public MentorshipRequestNotFoundException(Long id) {
        super(MESSAGE, BAD_REQUEST, id);
    }
}

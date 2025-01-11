package school.faang.user_service.exception.mentorship_request;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class EmptyMentorshipRequestDescriptionException extends ApiException {
    private static final String MESSAGE = "Description of mentorship request shouldn't be empty or null";

    public EmptyMentorshipRequestDescriptionException() {
        super(MESSAGE, BAD_REQUEST);
    }
}

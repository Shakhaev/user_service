package school.faang.user_service.exception.mentorship_request;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.CONFLICT;

public class UserRequestToHimselfException extends ApiException {
    private static final String MESSAGE = "User with id: %s cannot send a request to himself";

    public UserRequestToHimselfException(Long id) {
        super(MESSAGE, CONFLICT, id);
    }
}

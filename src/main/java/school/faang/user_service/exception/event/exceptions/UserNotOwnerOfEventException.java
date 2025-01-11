package school.faang.user_service.exception.event.exceptions;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class UserNotOwnerOfEventException extends ApiException {
    private static final String MESSAGE = "User with id: %s not owner of event with id: %s";

    public UserNotOwnerOfEventException(Long userId, Long eventId) {
        super(MESSAGE, BAD_REQUEST, userId, eventId);
    }
}

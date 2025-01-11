package school.faang.user_service.exception.event.exceptions;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class EventNotFoundException extends ApiException {
    private static final String MESSAGE = "Event with id: %s not found";

    public EventNotFoundException(Long id) {
        super(MESSAGE, NOT_FOUND, id);
    }
}

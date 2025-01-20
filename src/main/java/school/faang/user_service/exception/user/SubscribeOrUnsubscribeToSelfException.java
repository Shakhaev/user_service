package school.faang.user_service.exception.user;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class SubscribeOrUnsubscribeToSelfException extends ApiException {
    private static final String MESSAGE = "You cannot subscribe / unsubscribe to yourself.";

    public SubscribeOrUnsubscribeToSelfException() {
        super(MESSAGE, BAD_REQUEST);
    }
}

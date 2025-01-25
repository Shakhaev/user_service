package school.faang.user_service.exception.user;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class AlreadySubscribedToUserException extends ApiException {
    private static final String MESSAGE = "You have been already subscribed to the user with id: %s";

    public AlreadySubscribedToUserException(Long followeeId) {
        super(MESSAGE, BAD_REQUEST, followeeId);
    }
}

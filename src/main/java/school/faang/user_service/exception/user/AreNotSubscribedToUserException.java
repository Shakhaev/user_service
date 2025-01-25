package school.faang.user_service.exception.user;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class AreNotSubscribedToUserException extends ApiException {
    private static final String MESSAGE = "You are not subscribed to the user with id: %s";

    public AreNotSubscribedToUserException(Long followeeId) {
        super(MESSAGE, BAD_REQUEST, followeeId);
    }
}

package school.faang.user_service.exception.user;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class UsernameAlreadyExistsException extends ApiException {
    private static final String MESSAGE = "User with username: %s already exists";

    public UsernameAlreadyExistsException(String username) {
        super(MESSAGE, BAD_REQUEST, username);
    }
}

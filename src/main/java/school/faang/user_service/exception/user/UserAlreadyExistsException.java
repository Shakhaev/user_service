package school.faang.user_service.exception.user;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.CONFLICT;

public class UserAlreadyExistsException extends ApiException {
    private static final String MESSAGE = "User with %s already exists";

    public UserAlreadyExistsException(String existWith) {
        super(MESSAGE, CONFLICT, existWith);
    }
}

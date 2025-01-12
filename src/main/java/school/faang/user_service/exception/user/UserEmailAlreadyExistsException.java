package school.faang.user_service.exception.user;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class UserEmailAlreadyExistsException extends ApiException {
    private static final String MESSAGE = "User with email: %s already exists";

    public UserEmailAlreadyExistsException(String email) {
        super(MESSAGE, BAD_REQUEST, email);
    }
}

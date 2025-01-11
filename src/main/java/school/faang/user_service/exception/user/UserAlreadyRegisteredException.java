package school.faang.user_service.exception.user;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class UserAlreadyRegisteredException extends ApiException {
    private static final String MESSAGE = "User with id: %s already registered";

    public UserAlreadyRegisteredException(Long id) {
        super(MESSAGE, BAD_REQUEST, id);
    }
}

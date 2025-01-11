package school.faang.user_service.exception.user;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class UserNotFoundException extends ApiException {
    private static final String MESSAGE = "User with id %s not found";

    public UserNotFoundException(Long userId) {
        super(MESSAGE, NOT_FOUND, userId);
    }
}

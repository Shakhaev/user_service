package school.faang.user_service.exception.user;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.CONFLICT;

public class UserDeactivatedException extends ApiException {
    private static final String MESSAGE = "The user with id: %s has already been deactivated";

    public UserDeactivatedException(Long id) {
        super(MESSAGE, CONFLICT, id);
    }
}

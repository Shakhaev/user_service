package school.faang.user_service.exception.user;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class UserWasNotRegisteredException extends ApiException {
    private static final String MESSAGE = "User with id: %s wasn't registered";

    public UserWasNotRegisteredException(Long id) {
        super(MESSAGE, BAD_REQUEST, id);
    }
}

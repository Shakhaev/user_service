package school.faang.user_service.exception.user;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class UserIdListIsEmptyException extends ApiException {
    private static final String MESSAGE = "User ID list cannot be empty";

    public UserIdListIsEmptyException() {
        super(MESSAGE, BAD_REQUEST);
    }
}

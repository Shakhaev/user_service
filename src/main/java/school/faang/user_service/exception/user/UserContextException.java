package school.faang.user_service.exception.user;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class UserContextException extends ApiException {
    public UserContextException(String message) {
        super(message, BAD_REQUEST);
    }
}

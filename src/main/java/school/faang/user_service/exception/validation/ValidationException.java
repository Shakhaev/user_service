package school.faang.user_service.exception.validation;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class ValidationException extends ApiException {
    public ValidationException(String message) {
        super(message, BAD_REQUEST);
    }
}

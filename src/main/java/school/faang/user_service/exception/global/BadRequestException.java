package school.faang.user_service.exception.global;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class BadRequestException extends ApiException {
    public BadRequestException(String message, Object... args) {
        super(message, BAD_REQUEST, args);
    }
}

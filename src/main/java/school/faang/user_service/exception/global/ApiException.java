package school.faang.user_service.exception.global;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus httpStatus;

    public ApiException(String message, HttpStatus httpStatus, Object... args) {
        super(String.format(message, args));
        this.httpStatus = httpStatus;
    }
}

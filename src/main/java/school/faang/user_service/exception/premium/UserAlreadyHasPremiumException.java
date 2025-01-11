package school.faang.user_service.exception.premium;

import school.faang.user_service.exception.global.ApiException;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class UserAlreadyHasPremiumException extends ApiException {
    private static final String MESSAGE = "The user with id: %s already has a premium subscription before: %s";

    public UserAlreadyHasPremiumException(Long userId, LocalDateTime endDate) {
        super(MESSAGE, BAD_REQUEST, userId, endDate);
    }
}

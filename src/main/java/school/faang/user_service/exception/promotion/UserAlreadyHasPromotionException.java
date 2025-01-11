package school.faang.user_service.exception.promotion;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class UserAlreadyHasPromotionException extends ApiException {
    private static final String MESSAGE = "User with id: %s already has promotion, %S views left";

    public UserAlreadyHasPromotionException(Long userId, Integer viewsLeft) {
        super(MESSAGE, BAD_REQUEST, userId, viewsLeft);
    }
}

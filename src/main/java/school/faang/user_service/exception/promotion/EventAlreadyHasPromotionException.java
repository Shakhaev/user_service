package school.faang.user_service.exception.promotion;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class EventAlreadyHasPromotionException extends ApiException {
    private static final String MESSAGE = "Event with id: %s already has promotion, %S views left";

    public EventAlreadyHasPromotionException(Long eventId, Integer viewsLeft) {
        super(MESSAGE, BAD_REQUEST, eventId, viewsLeft);
    }
}

package school.faang.user_service.exception.payment;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class UnsuccessfulEventPromotionPaymentException extends ApiException {
    private static final String MESSAGE = "Payment by: %s views promotion for Event id: %s unsuccessful. Response message: %s";

    public UnsuccessfulEventPromotionPaymentException(Integer views, Long eventId, String responseMessage) {
        super(MESSAGE, INTERNAL_SERVER_ERROR, views, eventId, responseMessage);
    }
}

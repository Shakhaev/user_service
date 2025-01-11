package school.faang.user_service.exception.payment;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class UnsuccessfulUserPromotionPaymentException extends ApiException {
    private static final String MESSAGE = "Payment by: %s views promotion for User id: %s unsuccessful. Response message: %s";

    public UnsuccessfulUserPromotionPaymentException(Integer views, Long userId, String responseMessage) {
        super(MESSAGE, INTERNAL_SERVER_ERROR, views, userId, responseMessage);
    }
}

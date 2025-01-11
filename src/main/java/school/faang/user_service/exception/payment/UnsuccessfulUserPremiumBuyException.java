package school.faang.user_service.exception.payment;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class UnsuccessfulUserPremiumBuyException extends ApiException {
    private static final String MESSAGE = "Payment premium by period: %s days for User with id: %s unsuccessful, response message: %s";

    public UnsuccessfulUserPremiumBuyException(Integer period, Long userId, String responseMessage) {
        super(MESSAGE, INTERNAL_SERVER_ERROR, period, userId, responseMessage);
    }
}

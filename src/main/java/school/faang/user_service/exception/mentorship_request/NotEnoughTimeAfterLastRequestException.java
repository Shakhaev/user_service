package school.faang.user_service.exception.mentorship_request;

import school.faang.user_service.exception.global.ApiException;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

public class NotEnoughTimeAfterLastRequestException extends ApiException {
private static final String MESSAGE = "A request for %s can be made only %s time every %s";

    public NotEnoughTimeAfterLastRequestException(String requestFor, Integer times, String period) {
        super(MESSAGE, TOO_MANY_REQUESTS, requestFor, times, period);
    }
}

package school.faang.user_service.exception.recomendation.request;

import org.springframework.http.HttpStatus;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.global.ApiException;

public class RecommendationRequestRejectException extends ApiException {
    private final static String MESSAGE = "Recommendation request must have a status: %s";

    public RecommendationRequestRejectException(RequestStatus requestStatus) {
        super(MESSAGE, HttpStatus.CONFLICT, requestStatus.toString());
    }
}

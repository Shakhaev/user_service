package school.faang.user_service.exception.recomendation.request;

import org.springframework.http.HttpStatus;
import school.faang.user_service.exception.global.ApiException;

public class RecommendationRequestNotFoundException extends ApiException {
    private final static String MESSAGE = "Recommendation request with id: %s not found";

    public RecommendationRequestNotFoundException(Long id) {
        super(MESSAGE, HttpStatus.NOT_FOUND, id);
    }
}

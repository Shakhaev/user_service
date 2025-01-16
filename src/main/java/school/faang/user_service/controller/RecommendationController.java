package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequest;
import school.faang.user_service.dto.recommendation.CreateRecommendationResponse;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.exception.DataValidationException;

@Component
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public CreateRecommendationResponse giveRecommendation(CreateRecommendationRequest recommendationRequest) {
        if (validateRecommendation(recommendationRequest)) {
            return recommendationService.create(recommendationRequest);
        } else {
            throw new DataValidationException("Recommendation is empty");
        }
    }

    private boolean validateRecommendation(CreateRecommendationRequest recommendation) { // TODO: вынести валидацию в сервис?
        return !recommendation.getContent().isEmpty();
    }
}

package school.faang.user_service.controller;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

@Component
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {

        return null;
    }

    private boolean validateRecommendation(RecommendationDto recommendationDto) {

        return true;
    }
}

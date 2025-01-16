package school.faang.user_service.controller;

import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

@Component
public class RecommendationController {
    private RecommendationService recommendationService() {
        return null;
    }

    private RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        if(!recommendation.getContent().isEmpty()) {
            RecommendationService.create(recommendation);  // Как вызывать?
        } else {
            throw DataValidationException;
        }
        return //? как-то вернуть  RecommentationDto object without using 'new'.
    }
}

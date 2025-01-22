package school.faang.user_service.controller;

import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

@Controller
public class RecommendationController {
    RecommendationService recommendationService;

    private RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        if (recommendationDtoIsValid(recommendation)) {
            return recommendationService.create(recommendation);              // Как вызывать? Solved   //? как-то вернуть  RecommentationDto object without using 'new'.
        } else {
            throw new DataValidationException("Content is empty");
        }
    }

    private boolean recommendationDtoIsValid(RecommendationDto recommendationDto) {
        return !recommendationDto.getContent().isEmpty();
    }
}

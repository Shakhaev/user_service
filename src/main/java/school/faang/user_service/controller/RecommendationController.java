package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        if (validateRecommendation(recommendation)) {
            Optional<RecommendationDto> recommendationOptional = recommendationService.create(recommendation);

        } else {
            throw new DataValidationException("Recommendation is empty");
        }
    }

    private boolean validateRecommendation(RecommendationDto recommendation) { // TODO: вынести валидацию в сервис?
        return !recommendation.getContent().isEmpty();
    }
}

package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequest;
import school.faang.user_service.dto.recommendation.UpdateRecommendationRequest;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;
import java.time.Period;

import static java.time.LocalDateTime.now;

@Component
public class RecommendationValidator {
    public void validateRecommendationContentIsNotEmpty(CreateRecommendationRequest createRecommendationRequest) {
        if (createRecommendationRequest.getContent().isBlank())
            throw new DataValidationException("Recommendation content is empty");
    }

    public void validateRecommendationContentIsNotEmpty(UpdateRecommendationRequest updateRecommendationRequest) {
        if (updateRecommendationRequest.getContent().isBlank())
            throw new DataValidationException("Recommendation content is empty");
    }

    public void validateLastRecommendationTime(Recommendation lastRecommendation, LocalDateTime currentCreationTime) {
        Period timeDifference = Period.between(lastRecommendation.getCreatedAt().toLocalDate(), currentCreationTime.toLocalDate());
        if (timeDifference.getMonths() < 6) {
            throw new DataValidationException("The author can make a recommendation to the user no earlier than 6 months after the last recommendationDto. The last recommendationDto was given " + lastRecommendation.getCreatedAt());
        }
    }
}

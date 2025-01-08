package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;

    public Recommendation giveRecommendation(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        create(recommendation);
        return null;
    }

    public Long create(RecommendationDto recommendation) {
        return recommendationRepository.create(recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent());
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        if (!recommendation.getContent().isEmpty()) {
            this.create(recommendation);
        }
        throw new DataValidationException("Content is empty");
    }
}

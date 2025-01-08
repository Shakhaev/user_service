package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        create(recommendation);
        saveSkillOffer(recommendation);
        return recommendation;
    }

    private void saveSkillOffer(RecommendationDto recommendation) {
        recommendation.getSkillOffers()
                .forEach(skillOfferDto -> skillOfferRepository.create(skillOfferDto.getSkillId(), skillOfferDto.getRecommendationId()));
    }

    public RecommendationDto create(RecommendationDto recommendation) {
        if (recommendation.getCreatedAt().isBefore(ChronoLocalDateTime.from(LocalDateTime.now().minusMonths(6)))) {
            recommendationRepository.create(recommendation.getAuthorId(), recommendation.getReceiverId(), recommendation.getContent());
            return recommendation;
        }
        return recommendation;
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        if (!recommendation.getContent().isEmpty()) {
            this.create(recommendation);
        }
        throw new DataValidationException("Content is empty");
    }
}

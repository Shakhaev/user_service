package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        create(recommendation);
        saveSkillOffers(recommendation);

        return recommendation;
    }

    private void saveSkillOffers(RecommendationDto recommendation) {
        for (SkillOfferDto skillOffer : recommendation.getSkillOffers()) {

            int existingOffers = skillOfferRepository.countAllOffersOfSkill(
                    skillOffer.getSkillId(),
                    recommendation.getReceiverId()
            );

            if (existingOffers == 0) {
                skillOffer.getSkillId(); // TODO
            } else {
                skillOfferRepository.create(skillOffer.getSkillId(), skillOffer.getRecommendationId());
            }
        }

        recommendation.getSkillOffers()
                .forEach(skillOfferDto -> skillOfferRepository.create(skillOfferDto.getSkillId(), skillOfferDto.getRecommendationId()));
    }

    public RecommendationDto create(RecommendationDto recommendation) {
        if (recommendation.getCreatedAt() == null) {
            recommendation.setCreatedAt(LocalDateTime.now());
        }
        recommendationRepository
                .create(recommendation.getAuthorId(),
                        recommendation.getReceiverId(),
                        recommendation.getContent());

        return recommendation;
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        if (recommendation.getContent() == null || recommendation.getContent()
                .trim()
                .isEmpty()) {
            throw new DataValidationException("Recommendation content cannot be empty");
        }

        if (recommendation.getAuthorId() == null || recommendation.getReceiverId() == null) {
            throw new DataValidationException("Author and receiver must be specified");
        }

        if (recommendation.getAuthorId()
                .equals(recommendation.getReceiverId())) {
            throw new DataValidationException("Users cannot give recommendations to themselves");
        }

        checkForLastRecommendationPeriod(recommendation);

        if (recommendation.getSkillOffers() != null && !recommendation.getSkillOffers()
                .isEmpty()) {
            checkForExistingSkills(recommendation);
        }
    }

    private void checkForExistingSkills(RecommendationDto recommendation) {
        if (!recommendation.getSkillOffers()
                .stream()
                .allMatch(skillOfferDto ->
                        skillRepository.existsById(skillOfferDto.getSkillId())
                )) {
            throw new DataValidationException("These skills do not exists in system");
        }
    }

    private void checkForLastRecommendationPeriod(RecommendationDto recommendation) {
        recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendation.getAuthorId(),
                        recommendation.getReceiverId())
                .ifPresent(lastRecommendation -> {
                    if (lastRecommendation.getCreatedAt()
                            .plusMonths(6)
                            .isBefore(recommendation.getCreatedAt())) {
                        throw new DataValidationException("Recommendation can only be given after 6 months.");
                    }
                });
    }
}

package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class RecommendationServiceValidator {
    private final static int MONTHS_BEFORE_NEW_RECOMMENDATION = 6;
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;

    public void validateMonthsBetweenRecommendations(RecommendationDto recommendation) {
        boolean hasRecentRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendation.authorId(),
                        recommendation.receiverId()
                )
                .filter(r -> r.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(MONTHS_BEFORE_NEW_RECOMMENDATION)))
                .isPresent();

        if (hasRecentRecommendation) {
            throw new DataValidationException("You can only give a recommendation to the same user after 6 months.");
        }
    }

    public void validateSkillOffers(RecommendationDto recommendation) {
        boolean allSkillsExist = recommendation.skillOffers()
                .stream()
                .allMatch(skillOffer -> skillRepository.existsById(skillOffer.skillId()));

        if (!allSkillsExist) {
            throw new DataValidationException("Not all skills exist in the database");
        }

        boolean hasDuplicateSkills = recommendation.skillOffers().stream()
                .map(SkillOfferDto::skillId)
                .distinct()
                .count() != recommendation.skillOffers().size();

        if (hasDuplicateSkills) {
            throw new DataValidationException("Duplicate skills are not allowed in the recommendation.");
        }
    }
}

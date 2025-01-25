package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequest;
import school.faang.user_service.dto.recommendation.UpdateRecommendationRequest;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;

    public void validateRecommendation(Recommendation recommendation) {
        if (!checkRecommendationContentIsNotEmpty(recommendation)) {
            throw new DataValidationException("Recommendation content is empty");
        }

        if (!checkLastRecommendationTime(recommendation)) {
            throw new DataValidationException("The author can make a recommendation to the user " +
                    "no earlier than 6 months after the last recommendation");
            }
    }

    public void validateOfferedSkills(List<Long> skillIds) {
        if (!checkSkillsExist(skillIds)) {
            throw new DataValidationException("Skill doesn't exist");
        }
    }

    private boolean checkRecommendationContentIsNotEmpty(Recommendation recommendation) {
        return !recommendation.getContent().isBlank();
    }

    private boolean checkLastRecommendationTime(Recommendation recommendation) {
        Optional<Recommendation> lastRecommendation =
                recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendation.getAuthor().getId(),
                        recommendation.getReceiver().getId()
                );

        if (lastRecommendation.isPresent()) {
            Period timeDifference = Period.between(
                    lastRecommendation.get().getCreatedAt().toLocalDate(),
                    recommendation.getCreatedAt().toLocalDate()
            );
            return timeDifference.getMonths() >= 6;
        }

        return true;
    }

    private boolean checkSkillsExist(List<Long> skillIds) {
        for (Long skillId : skillIds) {
            if (!skillRepository.existsById(skillId))
                return false;
        }
        return true;
    }
}

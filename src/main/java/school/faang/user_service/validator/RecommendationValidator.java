package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.RecommendationService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {
    @Value("${recommendation.service.recommendation_period_in_month}")
    private int RECOMMENDATION_PERIOD_MONTHS;

    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public void validateRecommendation(RecommendationDto recommendationDto) {
        if (recommendationDto.getContent().isBlank()) {
            throw new DataValidationException("The recommendation content is empty");
        }
    }

    public void validateBeforeAction(RecommendationDto recommendationDto) {
        validateLastUpdate(recommendationDto);
        validateSkills(recommendationDto);
        validateSkillRepository(recommendationDto);
    }

    public void validateRecommendationById(long id) {
        if (!recommendationRepository.existsById(id)) {
            throw new DataValidationException("The recommendation doesn't exist in the system. ID : " + id);
        }
    }

    public void validateById(long id) {
        if (!userRepository.existsById(id)) {
            throw new DataValidationException("The user doesn't exist in the system. ID : " + id);
        }
    }
    private void validateLastUpdate(RecommendationDto recommendationDto) {
        long authorId = recommendationDto.getAuthorId();
        long receiverId = recommendationDto.getReceiverId();

        Optional<Recommendation> lastRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(authorId, receiverId);

        if (lastRecommendation.isPresent()) {
            LocalDateTime lastUpdate = lastRecommendation.get().getUpdatedAt();
            LocalDateTime nowDate = LocalDateTime.now();

            if (ChronoUnit.MONTHS.between(nowDate, lastUpdate) <= RECOMMENDATION_PERIOD_MONTHS) {
                String errorMessage = String.format("The author (ID : %d) cannot give a recommendation to a user (ID : %d)because it hasn't been %d months or more."
                        , authorId, receiverId, RECOMMENDATION_PERIOD_MONTHS);
                throw new DataValidationException(errorMessage);
            }
        }
    }

    private void validateSkills(RecommendationDto recommendationDto) {
        List<SkillOfferDto> skillOffers = recommendationDto.getSkillOffers();
        if ((skillOffers == null) || (skillOffers.isEmpty())) {
            throw new DataValidationException("The skill offers list is empty or null");
        }
    }

    private void validateSkillRepository(RecommendationDto recommendationDto) {
        List<SkillOfferDto> skillOffers = recommendationDto.getSkillOffers();

        List<Long> uniqueSkillOfferIds = skillOffers.stream()
                .map(SkillOfferDto::getSkillId).distinct().toList();

        for (var skillOfferId : uniqueSkillOfferIds) {
            if (!skillRepository.existsById(skillOfferId)) {
                String errorMessage =
                        String.format("The skill with ID : %d is doesn't exist in the system", skillOfferId);
                throw new DataValidationException(errorMessage);
            }
        }
    }

}

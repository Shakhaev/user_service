package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationDtoValidator {
    private static final int DIFFERENCE_BETWEEN_DATE_IN_MONTH = 6;
    private final RecommendationRepository recRepository;
    private final SkillRepository skillRepository;

    public void validateExistedSkillsAndDate(RecommendationDto recDto) {
        checkSkillOfferExists(recDto);
        checkDateTimeRecommendationOlderSixMonth(recDto);
    }

    private void checkSkillOfferExists(RecommendationDto recDto) {
        if (!recDto.getSkillOffers().isEmpty()) {
            List<String> skillTitlesList = recDto.getSkillOffers().stream()
                    .map(SkillOfferDto::getSkillTitle)
                    .toList();

            for (String skillTitle : skillTitlesList) {
                if (!skillRepository.existsByTitle(skillTitle)) {
                    log.error("Skill with title - {} does not exist in the system!", skillTitle);
                    throw new DataValidationException(String.format(ErrorMessage.SKILL_NOT_EXIST, skillTitle));
                }
            }
        }
    }

    private void checkDateTimeRecommendationOlderSixMonth(RecommendationDto recDto) {
        recRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recDto.getAuthorId(),
                recDto.getReceiverId()).ifPresent(recommendation -> {
            if (recommendation.getCreatedAt().isAfter(recDto.getCreatedAt().minusMonths(DIFFERENCE_BETWEEN_DATE_IN_MONTH))) {
                throw new DataValidationException(String.format(ErrorMessage.RECOMMENDATION_WRONG_TIME,
                        recDto.getAuthorId(), recDto.getReceiverId(), DIFFERENCE_BETWEEN_DATE_IN_MONTH));
            }
        });
    }
}

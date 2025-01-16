package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Service
@AllArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendation) {
        // автор дает рекомендацию не раньше, чем через 6 месяцев после его последней рекомендации этому пользователю.
        // Также проверить, что навыки, предлагаемые в рекомендации существуют в системе.
        Long newRecommendationId;
        if (LocalDateTime.now().minusMonths(6).isAfter(recommendation.getCreatedAt())
                && recommendationsArePresentedInSystem(recommendation.getSkillOffers())) {

            newRecommendationId = saveRecommendation(recommendation);
            // Сохранить предложенные в рекомендации скиллы в репозиторий SKillOfferRepository используя его метод create
            List<SkillOfferDto> skillOfferDtos = recommendation.getSkillOffers();
            if (!skillOfferDtos.isEmpty()) {
                for (SkillOfferDto skill : skillOfferDtos) {
                    saveSkillOffers(skill.getSkillId(), newRecommendationId);
                }

                // Если у пользователя, которому дают рекомендацию, такой скилл уже есть, то добавить автора рекомендации гарантом к скиллу,
                // который он предлагает, если этот автор еще не стоит там гарантом.
                List<Skill> userOldSkills = skillRepository.findAllByUserId(recommendation.getReceiverId());
                List<Skill> allSkillsGuaranteedToUserByGuarantee = userSkillGuaranteeRepository
                        .findAllSkillsGuaranteedToUserByGuarantee(
                                recommendation.getReceiverId(),
                                recommendation.getAuthorId());
                for (SkillOfferDto skill : skillOfferDtos) {
                    if (userOldSkills.contains(skill)) {
                        if (!allSkillsGuaranteedToUserByGuarantee.contains(skill)) {
                            addGuarantorToSkill(recommendation, skill);
                        }
                    } else { // добавление нового скила юзеру и прикрепление гаранта
                        skillRepository.assignSkillToUser(skill.getSkillId(), recommendation.getReceiverId());
                        addGuarantorToSkill(recommendation, skill);
                    }
                }
            }
            return new RecommendationDto(
                    newRecommendationId,
                    recommendation.getAuthorId(),
                    recommendation.getReceiverId(),
                    recommendation.getContent(),
                    recommendation.getSkillOffers(),
                    recommendation.getCreatedAt());
        } else {
            throw new DataValidationException("Skills are not presented in system " +
                    "or recommendation was given less than 6 months ago ");
        }
    }

    private boolean recommendationsArePresentedInSystem(List<SkillOfferDto> skillOfferDtos) {
        for (SkillOfferDto skill : skillOfferDtos) {
            if (!skillRepository.existsByTitle(skill.getTitle())) {
                return false;
            }
        }
        return true;
    }

    private Long saveRecommendation(RecommendationDto recommendationDto) {
        return recommendationRepository.create(recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
    }

    private void saveSkillOffers(Long skillId, Long newRecommendationId) {
        skillOfferRepository.create(skillId, newRecommendationId);
    }

    private void addGuarantorToSkill(RecommendationDto recommendationDto, SkillOfferDto skillOfferDto) {
        userSkillGuaranteeRepository.create(recommendationDto.getId(),
                skillOfferDto.getSkillId(),
                recommendationDto.getAuthorId());
    }
}

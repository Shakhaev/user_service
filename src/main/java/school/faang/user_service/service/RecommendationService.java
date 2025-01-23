package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
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
    RecommendationRepository recommendationRepository;
    SkillOfferRepository skillOfferRepository;
    SkillRepository skillRepository;
    UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    public RecommendationDto create(RecommendationDto recommendation) {
        // автор дает рекомендацию не раньше, чем через 6 месяцев после его последней рекомендации этому пользователю.
        // Также проверить, что навыки, предлагаемые в рекомендации существуют в системе.
        if (LocalDateTime.now().minusMonths(6).isAfter(recommendation.getCreatedAt())
                && recommendationsArePresentedInSystem(recommendation.getSkillOffers())) {

            Long newRecommendationId = recommendationRepository.create(recommendation.getAuthorId(),
                    recommendation.getReceiverId(),
                    recommendation.getContent());
            // Сохранить предложенные в рекомендации скиллы в репозиторий SKillOfferRepository используя его метод create
            List<SkillOfferDto> skillOfferDtos = recommendation.getSkillOffers();
            if (!skillOfferDtos.isEmpty()) {
                for (SkillOfferDto skill : skillOfferDtos) {
                    skillOfferRepository.create(skill.getSkillId(), newRecommendationId);//??? Как найти и передать skillId and recommendationId. Откуда их брать?
                }

                // Если у пользователя, которому дают рекомендацию, такой скилл уже есть, то добавить автора рекомендации гарантом к скиллу,
                // который он предлагает, если этот автор еще не стоит там гарантом.
                List<Skill> userOldSkills = skillRepository.findAllByUserId(recommendation.getReceiverId());
                List<Skill> allSkillsGuaranteedToUserByGuarantee = userSkillGuaranteeRepository.findAllSkillsGuaranteedToUserByGuarantee(
                        recommendation.getReceiverId(),
                        recommendation.getAuthorId());
                for (SkillOfferDto skill : skillOfferDtos) {
                    if (userOldSkills.contains(skill)) {
                        if(!allSkillsGuaranteedToUserByGuarantee.contains(skill)) {
                        // добавить автора рекомендации гарантом к скиллу
                        userSkillGuaranteeRepository.create(recommendation.getId(), skill.getSkillId(), recommendation.getAuthorId());
                        }
                    } else {
                        skillRepository.assignSkillToUser(skill.getSkillId(), recommendation.getReceiverId());
                        userSkillGuaranteeRepository.create(recommendation.getId(), skill.getSkillId(), recommendation.getAuthorId());
                    }
                }
            }


        }
        return recommendation; // ??? вернуть как-то RecommendationDto;
    }

    private boolean recommendationsArePresentedInSystem(List<SkillOfferDto> skillOfferDtos) {
        for (SkillOfferDto skill : skillOfferDtos) {
            if (!skillRepository.existsByTitle(skill.getTitle())) {
                return false;
            }
        }
        return true;
    }

    private List<Skill> skillsAlreadyBelongedToUser(Skill skill, RecommendationDto recommendationDto) {
        return skillRepository.findAllByUserId(recommendationDto.getReceiverId());
    }

    /// Если у пользователя, которому дают рекомендацию, такой скилл уже есть,
    /// то добавить автора рекомендации гарантом к скиллу, который он предлагает, если этот автор еще не стоит там гарантом.
    private boolean authorIsGuarantorForSkill(Skill skill, Long userId) {
        List<Skill> userSkill = skillRepository.findAllByUserId(userId);
        return false;
    }

    private void addAuthorAsGuarantorForSkill(Skill skill) {
    }

    private void saveSkillOffers() {
    }
}

package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.SkillRepository;
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

    public RecommendationDto create(RecommendationDto recommendation){
            if(LocalDateTime.now().minusMonths(6).isAfter(recommendation.getCreatedAt())
                && recommendationsArePresentedInSystem(recommendation.getSkillOffers())) {

                Long newRecommendationId = recommendationRepository.create(recommendation.getAuthorId(),
                            recommendation.getReceiverId(),
                            recommendation.getContent());

                List<SkillOfferDto> skillOfferDtos = recommendation.getSkillOffers();
                for(SkillOfferDto skill : skillOfferDtos) {
                    skillOfferRepository.create(skill.getSkillId(), newRecommendationId);//??? Как найти и передать skillId and recommendationId. Откуда их брать?
                }
            }
            return // ??? вернуть как-то RecommendationDto;
    }

    private boolean recommendationsArePresentedInSystem(List<SkillOfferDto> skillOfferDtos) {
        for(SkillOfferDto skill : skillOfferDtos){
            if(!skillRepository.existsByTitle(skill.getTitle())) {
                return false;
            }
        }
        return true;
    }

    private boolean authorIsGuarantorForSkill(Skill skill) {
        return false;
    }

    private void addAuthorAsGuarantorForSkill(Skill skill) {
    }

    private void saveSkillOffers() {
    }
}

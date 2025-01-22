package school.faang.user_service.service;

import lombok.Getter;
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
public class RecommendationService {
    RecommendationRepository recommendationRepository;
    SkillOfferRepository skillOfferRepository;
    public RecommendationDto create(RecommendationDto recommendation){
            if(LocalDateTime.now().minusMonths(6).isAfter(recommendation.getCreatedAt())
            && recommendationsArePresentedInSystem(recommendation.getSkillOffers())) {
                // ? Как проверить присутствует ли skill в системе (Базе данных)
                // ? Как создать как-то новую рекомендацию
                /// Трудность - в подходе Spring стала непонятно, как обращаться объектам классов, как и где их создавать,
                /// как осуществлять поиск. Это как учить новый язык программирования, который рабтает на других
                /// правилах взаимодействия и хранения объектов.

                skillOfferRepository.create(0,0); //??? Как найти и передать skillId and recommendationId
                // ??? как создать экземпляр класса не использую оператор 'new'?

                // Если skill уже есть - добавить автора гарантом этого скила
                // проверка на наличие скила у пользователя
                // Добавление автора рекомендации гарантом к рекомендованному скиллу

                if(/*allchecks are passed*/) {
                    recommendationRepository.create()
                }
            }
            return // ??? вернуть как-то RecommendationDto;
    }

    private boolean recommendationsArePresentedInSystem(List<SkillOfferDto> skillOfferDtos) {
        List<SkillOfferDto> skills = //SkillRepository// как вызвать SkillRepository.findSkillsOfferedToUser  ???
        for(SkillOfferDto skill : skillOfferDtos){
            if(!skills.contains(skill)) {
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

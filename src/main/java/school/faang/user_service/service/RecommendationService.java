package school.faang.user_service.service;

import lombok.Getter;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import java.time.LocalDateTime;

@Getter
@Component
public class RecommendationService {
    RecommendationRepository recommendationRepository;
    SkillOfferRepository skillOfferRepository;
    public static void create(RecommendationDto recommendation){
            if(LocalDateTime.now().minusMonths(6).isAfter(recommendation.getCreatedAt())) {
                // ? Как проверить присутствует ли skill в системе (Базе данных)
                // ? Как создать как-то новую рекомендацию
                /// Трудность - в подходе Spring стала непонятно, как обращаться объектам классов, как и где их создавать,
                /// как осуществлять поиск. Это как учить новый язык программировани, который рабтает на других
                /// правилах взаимодействия и хранения объектов.
                SkillOfferRepository.create(); // ? как создать экземпляр класса не использую оператор 'new'?

                // Если skill уже есть - добавить автора гарантом этого скила
                // проверка на наличие скила у пользователя
                // Добавление автора рекомендации гарантом к рекомендованному скиллу

                if(/*allchecks are passed*/) {
                    recommendationRepository.create()
                }
            }
    }
}

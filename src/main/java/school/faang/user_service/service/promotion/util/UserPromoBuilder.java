package school.faang.user_service.service.promotion.util;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;

import java.time.LocalDateTime;

@Component
public class UserPromoBuilder {
    public UserPromotion buildUserPromotion(User user, PromotionTariff tariff) {
        return UserPromotion
                .builder()
                .promotionTariff(tariff)
                .cost(tariff.getCost())
                .currency(tariff.getCurrency())
                .coefficient(tariff.getCoefficient())
                .user(user)
                .numberOfViews(tariff.getNumberOfViews())
                .audienceReach(tariff.getAudienceReach())
                .creationDate(LocalDateTime.now())
                .build();
    }
}

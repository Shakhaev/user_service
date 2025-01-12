package school.faang.user_service.service.promotion.util;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;

import java.time.LocalDateTime;

@Component
public class EventPromoBuilder {
    public EventPromotion buildEventPromotion(Event event, PromotionTariff tariff) {
        return EventPromotion
                .builder()
                .promotionTariff(tariff)
                .cost(tariff.getCost())
                .currency(tariff.getCurrency())
                .coefficient(tariff.getCoefficient())
                .event(event)
                .numberOfViews(tariff.getNumberOfViews())
                .audienceReach(tariff.getAudienceReach())
                .creationDate(LocalDateTime.now())
                .build();
    }
}

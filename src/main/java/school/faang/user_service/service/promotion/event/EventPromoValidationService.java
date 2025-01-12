package school.faang.user_service.service.promotion.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.exception.event.exceptions.UserNotOwnerOfEventException;
import school.faang.user_service.exception.promotion.EventAlreadyHasPromotionException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EventPromoValidationService {
    public void checkEventForUserAndPromotion(long userId, long eventId, Event event) {
        if (userId != event.getOwner().getId()) {
            throw new UserNotOwnerOfEventException(userId, eventId);
        }
        getActiveEventPromotion(event).ifPresent(promotion -> {
            throw new EventAlreadyHasPromotionException(eventId, promotion.getNumberOfViews());
        });
    }

    public Optional<EventPromotion> getActiveEventPromotion(Event event) {
        return event.getPromotions()
                .stream()
                .filter(promotion -> promotion.getNumberOfViews() > 0)
                .findFirst();
    }

    public List<EventPromotion> getActiveEventPromotions(List<Event> events) {
        return events.stream()
                .map(this::getActiveEventPromotion)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}

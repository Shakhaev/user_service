package school.faang.user_service.service.promotion;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.exception.event.exceptions.UserNotOwnerOfEventException;
import school.faang.user_service.exception.promotion.EventAlreadyHasPromotionException;
import school.faang.user_service.exception.promotion.UserAlreadyHasPromotionException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PromotionValidationService {

    public void checkUserForPromotion(User user) {
        getActiveUserPromotion(user).ifPresent(promotion -> {
            throw new UserAlreadyHasPromotionException(user.getId(), promotion.getNumberOfViews());
        });
    }

    public Optional<UserPromotion> getActiveUserPromotion(User user) {
        return user.getPromotions()
                .stream()
                .filter(promotion -> promotion.getNumberOfViews() > 0)
                .findFirst();
    }

    public List<UserPromotion> getActiveUserPromotions(List<User> users) {
        return users.stream()
                .map(this::getActiveUserPromotion)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

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

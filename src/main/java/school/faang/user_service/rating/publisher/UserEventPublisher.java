package school.faang.user_service.rating.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.rating.ActionType;
import school.faang.user_service.rating.observer.EventObserver;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserEventPublisher {
    private final Map<ActionType, EventObserver> observers;

    public UserEventPublisher(List<EventObserver> observerList) {
        this.observers = observerList.stream()
                .collect(Collectors.toMap(EventObserver::getSupportedActionType, observer -> observer));
    }

    public void publishEvent(ActionType actionType, long followeeId) {
        EventObserver observer = observers.get(actionType);
        if (observer != null) {
            observer.observe(followeeId);
        } else {
            throw new IllegalArgumentException("Нет обработчика для события: " + actionType);
        }
    }
}

package school.faang.user_service.rating.observer;

import school.faang.user_service.rating.ActionType;

public interface EventObserver {
    ActionType getSupportedActionType();
    void observe(long userId);
}

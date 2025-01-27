package school.faang.user_service.rating.observer.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.AppConfig;
import school.faang.user_service.dto.rating.RatingDto;
import school.faang.user_service.rating.ActionType;
import school.faang.user_service.rating.observer.EventObserver;

@Component
@RequiredArgsConstructor
public class FollowObserver implements EventObserver {
    private final ApplicationEventPublisher publisher;
    private final AppConfig appConfig;

    @Override
    public ActionType getSupportedActionType() {
        return ActionType.FOLLOW;
    }

    @Override
    public void observe(long followeeId) {
        RatingDto ratingDto = RatingDto.builder()
                .descriptionable(u -> "User : " + u.getUsername() + " -> followed user and got rating!")
                .id(followeeId)
                .points(appConfig.getPassiveTransaction())
                .actionType(ActionType.FOLLOW)
                .build();

        publisher.publishEvent(ratingDto);
    }
}

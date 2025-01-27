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
public class SkillAcquireObserver implements EventObserver {
    private final ApplicationEventPublisher publisher;
    private final AppConfig appConfig;

    @Override
    public ActionType getSupportedActionType() {
        return ActionType.SKILL_ACQUIRE;
    }

    @Override
    public void observe(long followeeId) {
        RatingDto ratingDto = RatingDto.builder()
                .descriptionable(u -> "User : " + u.getUsername() + " -> acquired skill and got rating!")
                .id(followeeId)
                .points(appConfig.getPassiveTransaction())
                .actionType(ActionType.SKILL_ACQUIRE)
                .build();

        publisher.publishEvent(ratingDto);
    }
}

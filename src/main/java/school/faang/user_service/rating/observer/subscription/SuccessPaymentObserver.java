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
public class SuccessPaymentObserver implements EventObserver {
    private final ApplicationEventPublisher publisher;
    private final AppConfig appConfig;

    @Override
    public ActionType getSupportedActionType() {
        return ActionType.PAYMENT_SUCCESS;
    }

    @Override
    public void observe(long followeeId) {
        RatingDto ratingDto = RatingDto.builder()
                .descriptionable(u -> "User : " + u.getUsername() + " -> made success payment and got rating!")
                .id(followeeId)
                .points(appConfig.getActiveTransaction())
                .actionType(ActionType.PAYMENT_SUCCESS)
                .build();

        publisher.publishEvent(ratingDto);
    }
}

package school.faang.user_service.message.event;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationReceivedEvent extends NotificationEvent {
    private long recommenderUserId;
    private long recommendationId;
}

package school.faang.user_service.event;

import java.time.LocalDateTime;

public record SubscriptionEvent(
        Long followerId,
        Long followeeId,
        LocalDateTime subscribedAt,
        String followerName,
        String followeeName
) {
}
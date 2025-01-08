package school.faang.user_service.dto.subscription;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record SubscriptionFollowDto(
        @NotNull
        @PositiveOrZero
        Long followerId,
        @NotNull
        @PositiveOrZero
        Long followeeId
) {
}

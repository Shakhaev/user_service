package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FollowingFeatureDto(@Positive @NotNull long followerId, @Positive @NotNull long followeeId) {
}

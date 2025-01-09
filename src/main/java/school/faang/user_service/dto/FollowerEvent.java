package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FollowerEvent(
        @NotNull Long followerId,
        Long followeeId,
        Long projectId,
        @NotNull LocalDateTime eventTime
) {
}

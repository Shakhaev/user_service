package school.faang.user_service.dto.events;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record RecommendationReceivedEventDto(
        @NotNull Long recommendationId,
        @NotNull Long receiverId,
        @NotNull Long authorId,
        @NotNull String authorName,
        @NotNull LocalDateTime createdAt
) implements Event {

}

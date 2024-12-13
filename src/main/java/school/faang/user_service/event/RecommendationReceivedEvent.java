package school.faang.user_service.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RecommendationReceivedEvent {
    private final Long recommendationId;
    private final Long receiverId;
    private final String receiverName;
    private final Long authorId;
    private final String authorName;
}

package school.faang.user_service.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationReceivedEvent {
    private Long authorId;
    private Long receiverId;
    private Long recommendationId;
}

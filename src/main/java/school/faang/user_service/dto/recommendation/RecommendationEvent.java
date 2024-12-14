package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationEvent {
    private Long id;
    private Long actorId;
    private Long receiverId;
    private LocalDateTime receivedAt;
}

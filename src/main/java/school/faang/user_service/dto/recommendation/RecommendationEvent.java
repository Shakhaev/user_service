package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationEvent {

    private Long id;

    private Long authorId;

    private Long receiverId;

    private LocalDateTime createTime;
}

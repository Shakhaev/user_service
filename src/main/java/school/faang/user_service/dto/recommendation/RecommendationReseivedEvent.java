package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationReseivedEvent {
    private Long id;
    private Long authorId;
    private Long recipientId;
}

package school.faang.user_service.dto.recommendation;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateRecommendationResponse {
    private Long id;
    private Long authorId;
    private Long receiverId;
    private String content;
    private List<Long> skillOfferIds;
    private LocalDateTime createdAt;
}
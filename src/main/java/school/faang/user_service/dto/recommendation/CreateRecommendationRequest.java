package school.faang.user_service.dto.recommendation;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class CreateRecommendationRequest extends RecommendationDto {
    private Long authorId;
    private Long receiverId;
    private String content;
    private List<Long> skillIds;
}

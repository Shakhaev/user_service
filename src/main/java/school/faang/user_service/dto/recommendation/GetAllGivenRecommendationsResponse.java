package school.faang.user_service.dto.recommendation;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class GetAllGivenRecommendationsResponse extends RecommendationDto {
    private Long id;
    private Long authorId;
    private Long receiverId;
    private String content;
    private List<Long> skillOfferIds;
}

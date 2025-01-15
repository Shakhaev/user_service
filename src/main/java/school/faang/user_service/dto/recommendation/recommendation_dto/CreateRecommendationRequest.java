package school.faang.user_service.dto.recommendation.recommendation_dto;

import lombok.Data;
import school.faang.user_service.dto.recommendation.SkillOfferDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateRecommendationRequest {
    private Long authorId;
    private Long receiverId;
    private String content;
    private List<SkillOfferDto> skillOffers;
    private LocalDateTime createdAt;
}

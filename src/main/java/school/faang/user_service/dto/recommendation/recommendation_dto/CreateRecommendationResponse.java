package school.faang.user_service.dto.recommendation.recommendation_dto;

import lombok.Data;
import school.faang.user_service.dto.recommendation.skill_offer_dto.CreateSkillOfferResponse;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateRecommendationResponse {
    private long id;
    private Long authorId;
    private Long receiverId;
    private String content;
    private List<CreateSkillOfferResponse> skillOffers;
    private LocalDateTime createdAt;
}

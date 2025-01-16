package school.faang.user_service.dto.recommendation.recommendation_dto;

import lombok.Data;
import school.faang.user_service.dto.recommendation.skill_offer_dto.CreateSkillOfferRequest;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateRecommendationRequest {
    private Long authorId;
    private Long receiverId;
    private String content;
    private List<CreateSkillOfferRequest> skillOffers;
    private LocalDateTime createdAt;
}

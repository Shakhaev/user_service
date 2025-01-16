package school.faang.user_service.dto.recommendation;

import lombok.Data;
import school.faang.user_service.dto.skill_offer.CreateSkillOfferRequest;

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

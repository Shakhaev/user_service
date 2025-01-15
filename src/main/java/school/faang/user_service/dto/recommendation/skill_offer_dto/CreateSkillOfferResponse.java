package school.faang.user_service.dto.recommendation.skill_offer_dto;

import lombok.Data;

@Data
public class CreateSkillOfferResponse {
    private Long id;
    private Long skillId;
    private Long recommendationId;
}

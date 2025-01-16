package school.faang.user_service.dto.recommendation.skill_offer_dto;

import lombok.Data;

@Data
public class CreateSkillOfferResponse {
    private long id;
    private long skillId;
    private long recommendationId;
}

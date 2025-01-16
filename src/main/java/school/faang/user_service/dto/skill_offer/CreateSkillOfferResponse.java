package school.faang.user_service.dto.skill_offer;

import lombok.Data;

@Data
public class CreateSkillOfferResponse {
    private long id;
    private long skillId;
    private long recommendationId;
}

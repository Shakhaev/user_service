package school.faang.user_service.dto.recommendation;

import lombok.Builder;

@Builder
public record SkillOfferDto(
        long id,
        long skillId
) {
}

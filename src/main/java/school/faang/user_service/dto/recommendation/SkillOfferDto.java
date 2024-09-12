package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillOfferDto {
    @Positive(message = "ID must be positive")
    private Long skillId;

    @Positive(message = "RecommendationID must be positive")
    private Long recommendationId;
}

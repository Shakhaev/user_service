package school.faang.user_service.dto.recommendation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "Skill offer")
@Data
public class SkillOfferDto {
    @Schema(description = "Skill offer ID")
    Long id;

    @Schema(description = "Skill ID", example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "The skillId cannot be null")
    Long skillId;

    @Schema(description = "Recommendation ID")
    Long recommendationId;
}

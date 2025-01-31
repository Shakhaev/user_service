package school.faang.user_service.dto.recommendation;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Recommendation")
@Data
public class RecommendationDto {
    @Schema(description = "Recommendation ID", example = "1")
    Long id;

    @Schema(description = "Recommendation author's ID", example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "The recommendation must have an authorId")
    Long authorId;

    @Schema(description = "Recommendation receiver's ID", example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "The recommendation must have an receiverId")
    Long receiverId;

    @Schema(description = "List of offered skills",
            example = "[{\"skillId\": 1}, {\"skillId\": 2}]")
    @ArraySchema(
            schema = @Schema(implementation = SkillOfferDto.class),
            minItems = 1,
            uniqueItems = true
    )
    List<SkillOfferDto> skillOffers;

    @Schema(description = "Content of the recommendation",
            example = "Example content", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "The recommendation should have a content")
    @NotNull(message = "The content cannot be null")
    String content;

    @Schema(description = "Date and time of creation recommendation",
            accessMode = Schema.AccessMode.READ_ONLY)
    LocalDateTime createdAt;
}

package school.faang.user_service.dto.recommendation;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record RecommendationDto(
        @Nullable
        Long id,
        @NotNull(message = "Отсутствует authorId")
        @Min(1)
        Long authorId,
        @NotNull(message = "Отсутствует receiverId")
        @Min(1)
        Long receiverId,
        @NotBlank(message = "Поле content не может быть пустым")
        String content,
        @Nullable
        List<SkillOfferDto> skillOffers
) {

}

package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillOfferDto {
    @NotNull
    @Positive(message = "Id должно быть позитивным числом")
    private Long id;

    @NotNull
    @Positive(message = "Id должно быть позитивным числом")
    private Long skillId;
}

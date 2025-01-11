package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDto {
    @Positive(message = "Id должно быть позитивным числом")
    private Long id;

    @NotNull
    @Positive(message = "Id должно быть позитивным числом")
    private Long authorId;

    @NotNull
    @Positive(message = "Id должно быть позитивным числом")
    private Long receiverId;

    @NotBlank(message = "Рекомендация не должна быть пустой!")
    private String content;

    @NotNull
    private List<SkillOfferDto> skillOffers;
    private LocalDateTime createdAt;
}

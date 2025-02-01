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
    @Positive(message = "Id должно быть больше нуля")
    private Long id;

    @NotNull(message = "Id не должно равняться нулю")
    @Positive(message = "Id должно быть больше нуля")
    private Long authorId;

    @NotNull(message = "Id не должно равняться нулю")
    @Positive(message = "Id должно быть больше нуля")
    private Long receiverId;

    @NotBlank(message = "Рекомендация не должна быть пустой!")
    private String content;

    @NotNull(message = "Список не должен быть пустым")
    private List<SkillOfferDto> skillOffers;
    private LocalDateTime createdAt;
}

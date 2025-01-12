package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationDto {
    private Long id;
    @NotNull(message = "Author ID must not be null")
    private Long authorId;
    @NotNull(message = "Receiver ID must not be null")
    private Long receiverId;
    @NotNull(message = "Content must not be null")
    @NotBlank(message = "Content must not be blank")
    private String content;
    @NotNull(message = "Skill offers must not be null")
    @NotEmpty(message = "Skill offers must not be empty")
    private List<SkillOfferDto> skillOffers;
    @NotNull(message = "Request id must not be null")
    private Long requestId;
    private LocalDateTime createdAt;
}

package school.faang.user_service.dto.recommendation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RecommendationRequestDto {
    @NotNull
    @NotBlank
    private String message;
    private List<Long> skillIds;
    private Long requesterId;
    private Long receiverId;
}

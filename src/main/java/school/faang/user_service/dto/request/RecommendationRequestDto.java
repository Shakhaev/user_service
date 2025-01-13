package school.faang.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;

@Data
public class RecommendationRequestDto {
    private Long requesterId;
    private Long receiverId;

    @NotBlank
    private String message;

    private List<Long> skillsIds;
}

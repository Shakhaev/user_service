package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private Long id;
    @NotNull
    @NotBlank
    private String message;
    private RequestStatus status;
    private String rejectionReason;
    private List<Long> skillIds;
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

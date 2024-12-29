package school.faang.user_service.dto.recommendation;

import lombok.Data;
import school.faang.user_service.dto.RequestStatusDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationRequestDto {
    private Long id;
    private String message;
    private RequestStatusDto status;
    private String rejectionReason;
    private List<Long> skillsId;
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

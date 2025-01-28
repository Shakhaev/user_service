package school.faang.user_service.dto.recommendation.response;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecommendationResponseDto {
    private long id;
    private String message;
    private RequestStatus status;
    private String rejectionReason;
    private List<Long> skillIds;
    private Long requesterId;
    private Long receiverId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

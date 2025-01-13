package school.faang.user_service.dto.respones;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RecommendationResponseDto {
    private Long id;
    private Long requesterId;
    private Long receiverId;
    private List<Long> skillsRequests;
    private String message;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

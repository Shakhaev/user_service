package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotEmpty;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;

public record RecommendationRequestDto(@NotEmpty
                                       String message,
                                       RequestStatus status,
                                       List<Long> skills,
                                       long requesterId,
                                       long receiverId) {
}

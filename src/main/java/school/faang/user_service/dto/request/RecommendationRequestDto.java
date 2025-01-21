package school.faang.user_service.dto.request;

import java.util.List;

public record RecommendationRequestDto(Long requesterId, Long receiverId, String message, List<Long> skillsIds) {
}

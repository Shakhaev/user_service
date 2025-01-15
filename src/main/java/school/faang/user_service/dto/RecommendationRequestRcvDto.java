package school.faang.user_service.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record RecommendationRequestRcvDto(
        String message,
        List<Long> skillIds,
        Long requesterId,
        Long receiverId
) {}

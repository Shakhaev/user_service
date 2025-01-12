package school.faang.user_service.dto.recommendation;

import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;

import java.util.List;

public record RecommendationRequestDto(long id,
                                       String message,
                                       RequestStatus status,
                                       List<Skill> skills,
                                       long requesterId,
                                       long receiverId) {
}

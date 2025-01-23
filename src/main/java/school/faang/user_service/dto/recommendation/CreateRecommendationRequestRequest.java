package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateRecommendationRequestRequest(@NotEmpty String message,
                                                 List<Long> skills,
                                                 long requesterId,
                                                 long receiverId) {
}

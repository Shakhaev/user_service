package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import school.faang.user_service.entity.goal.GoalStatus;

/**
 * DTO for {@link school.faang.user_service.entity.goal.Goal}
 */
@Builder
public record GoalFilterDto(@PositiveOrZero Long parentId,
                            String title,
                            String description,
                            GoalStatus status,
                            @PositiveOrZero Long mentorId) {
}
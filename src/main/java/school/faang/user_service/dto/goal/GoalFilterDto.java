package school.faang.user_service.dto.goal;

import school.faang.user_service.entity.goal.GoalStatus;

/**
 * DTO for {@link school.faang.user_service.entity.goal.Goal}
 */
public record GoalFilterDto(Long parentId,
                            String title,
                            String description,
                            GoalStatus status,
                            Long mentorId) {
}
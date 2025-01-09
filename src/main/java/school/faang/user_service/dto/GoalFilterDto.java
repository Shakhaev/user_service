package school.faang.user_service.dto;

import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

/**
 * DTO for {@link school.faang.user_service.entity.goal.Goal}
 */
public record GoalFilterDto(Long parentId,
                            String title,
                            String description,
                            GoalStatus status,
                            Long mentorId) {
}
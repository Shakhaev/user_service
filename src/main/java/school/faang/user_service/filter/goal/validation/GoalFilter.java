package school.faang.user_service.filter.goal.validation;

import school.faang.user_service.entity.goal.Goal;

public interface GoalFilter {
    void apply(Goal goal, Long userId, Long invitedUserId);
}
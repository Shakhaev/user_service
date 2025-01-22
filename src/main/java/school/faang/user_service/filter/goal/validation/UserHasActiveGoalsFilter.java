package school.faang.user_service.filter.goal.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.InvalidInvitationException;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@RequiredArgsConstructor
public class UserHasActiveGoalsFilter implements GoalFilter {

    private static final int MAX_ACTIVE_GOALS = 3;

    private final GoalRepository goalRepository;

    @Override
    public void apply(Goal goal, Long userId, Long invitedUserId) {
        int activeGoals = goalRepository.countActiveGoalsPerUser(userId);
        if (activeGoals >= MAX_ACTIVE_GOALS) {
            throw new InvalidInvitationException("User already has the maximum number of active goals.");
        }
    }
}
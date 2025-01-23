package school.faang.user_service.filter.goal.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.InvalidInvitationException;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@RequiredArgsConstructor
public class UserNotWorkingOnGoalFilter implements GoalFilter {

    private final GoalRepository goalRepository;

    @Override
    public void apply(Goal goal, Long userId, Long invitedUserId) {
        boolean alreadyWorking = goalRepository.findUsersByGoalId(goal.getId())
                .stream()
                .anyMatch(user -> user.getId().equals(invitedUserId));

        if (alreadyWorking) {
            throw new InvalidInvitationException("User is already working on this goal.");
        }
    }
}
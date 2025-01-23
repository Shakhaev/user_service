package school.faang.user_service.filter.goal.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.InvalidInvitationException;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@RequiredArgsConstructor
public class GoalExistsFilter implements GoalFilter {

    private final GoalRepository goalRepository;

    @Override
    public void apply(Goal goal, Long userId, Long invitedUserId) {
        if (!goalRepository.existsById(goal.getId())) {
            throw new InvalidInvitationException("Goal does not exist.");
        }
    }
}
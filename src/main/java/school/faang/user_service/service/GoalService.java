package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;

    public Goal getGoalById(long goalId) {
        return goalRepository.findById(goalId).orElseThrow(() -> new IllegalArgumentException("There is no goal with id: " + goalId));
    }

    public Goal updateGoal(Goal goal) {
        return goalRepository.save(goal);
    }
}

package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    @Transactional
    public List<Goal> stopGoalsByUser(Long userId) {
        List<Goal> goals = goalRepository.findGoalsByUserId(userId).toList();
        goals.forEach(goal -> {
            if (goal.getUsers().size() <= 1) {
                goalRepository.delete(goal);
            } else {
                goal.getUsers().removeIf(user -> user.getId().equals(userId));
            }
        });

        return goals;
    }
}

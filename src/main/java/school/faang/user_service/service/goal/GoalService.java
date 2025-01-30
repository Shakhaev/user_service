package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.GoalCannotBeCompletedException;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    @Transactional
    public void completeGoalsByUser(Long userId) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);
        goals.forEach(goal -> {
            if (goal.getUsers().size() <= 1) {
                goal.setStatus(GoalStatus.COMPLETED);
                goalRepository.save(goal);
                goalRepository.delete(goal);
            } else {
                throw new GoalCannotBeCompletedException("Goal cannot be completed because it has other participants");
            }
        });
    }
}

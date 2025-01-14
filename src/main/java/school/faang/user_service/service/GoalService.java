package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    @Transactional
    public void deactivateGoalsByUser(Long userId) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);

        goals.forEach(goal -> {
            if (goal.getUsers().size() <= 1) {
                goalRepository.delete(goal);
            } else if (goal.getStatus() == GoalStatus.ACTIVE) {
                goal.setStatus(GoalStatus.COMPLETED);
                goalRepository.save(goal);
            } else {
                throw new BusinessException("Цель имеет других участников и не может быть завершена.");
            }
        });
    }
}

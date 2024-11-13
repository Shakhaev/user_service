package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class GoalService {
    private final GoalRepository goalRepository;

    public Goal getGoalById(long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Goal do not found"));
    }

    public void removeGoalsWithoutExecutingUsers(List<Goal> goals) {
        goals.stream()
                .filter(Goal::isEmptyExecutingUsers)
                .forEach(goal -> goalRepository.deleteById(goal.getId()));
        log.info("Goals without users is removed");
    }

    public List<Goal> mapListIdsToGoals(List<Long> goalsIds) {
        return goalsIds.stream()
                .map(id -> getGoalById(id))
                .toList();
    }
}
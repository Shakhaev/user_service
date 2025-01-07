package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    public Goal findById(long id) {
        return goalRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Goal with id = " + id + " doesn't exists"));
    }

    public boolean existsById(Long id) {
        return goalRepository.existsById(id);
    }

    public void update(Goal newGoal) {
        goalRepository.save(newGoal);
    }
}

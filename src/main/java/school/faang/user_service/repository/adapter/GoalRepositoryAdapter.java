package school.faang.user_service.repository.adapter;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@RequiredArgsConstructor
public class GoalRepositoryAdapter {
    private final GoalRepository goalRepository;

    public Goal getById(Long id) {
        return goalRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Goal not found by id: " + id));
    }
}

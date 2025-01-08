package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDTO;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;
@Component
public class GoalStatusFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDTO goalFilterDTO) {
        return goalFilterDTO.getStatus() != null;
    }

    @Override
    public List<Goal> apply(List<Goal> goals, GoalFilterDTO goalFilterDTO) {
        return goals.stream()
                .filter(goal -> goal.getStatus().name().equals(goalFilterDTO.getStatus()))
                .toList();
    }
}

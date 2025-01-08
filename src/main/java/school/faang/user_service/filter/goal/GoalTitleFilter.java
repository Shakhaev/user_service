package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDTO;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Component
public class GoalTitleFilter implements GoalFilter{
    @Override
    public boolean isApplicable(GoalFilterDTO goalFilterDTO) {
        return goalFilterDTO.getTitle() != null;
    }

    @Override
    public List<Goal> apply(List<Goal> goals, GoalFilterDTO goalFilterDTO) {
        return goals.stream()
                .filter(goal -> goal.getTitle().equals(goalFilterDTO.getTitle()))
                .toList();
    }
}

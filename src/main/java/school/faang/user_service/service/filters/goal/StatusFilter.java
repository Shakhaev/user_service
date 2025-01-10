package school.faang.user_service.service.filters.goal;

import org.springframework.context.annotation.Configuration;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Configuration
public class StatusFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filters) {
        return goals.filter(goal -> goal.getStatus().equals(filters.getStatus()));
    }
}

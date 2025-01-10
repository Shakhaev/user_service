package school.faang.user_service.service.filters.goal;

import org.springframework.context.annotation.Configuration;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Configuration
public interface GoalFilter {
    boolean isApplicable(GoalFilterDto filters);

    Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filters);
}
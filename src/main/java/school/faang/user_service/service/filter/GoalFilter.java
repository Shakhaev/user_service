package school.faang.user_service.service.filter;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

public interface GoalFilter {
    boolean isApplicable(GoalFilterDto filterDto);

    Stream<Goal> apply(Stream<Goal> goalStream, GoalFilterDto filterDto);
}

package school.faang.user_service.service.goal.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.stream.Stream;

@Component
public class DescriptionFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto filterDto) {
        return filterDto.description() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goalStream, GoalFilterDto filterDto) {
        return goalStream.filter(goal -> goal.getDescription().equals(filterDto.description()));
    }
}

package school.faang.user_service.service.goal.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.filter.fiilterabs.GoalFilter;

import java.util.stream.Stream;

@Component
public class StatusFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto filterDto) {
        return filterDto.status() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goalStream, GoalFilterDto filterDto) {
        return goalStream.filter(goal -> goal.getStatus().equals(filterDto.status()));
    }
}

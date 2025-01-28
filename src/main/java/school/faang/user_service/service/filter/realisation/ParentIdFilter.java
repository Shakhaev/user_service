package school.faang.user_service.service.filter.realisation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.filter.GoalFilter;

import java.util.stream.Stream;

@Component
public class ParentIdFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto filterDto) {
        return filterDto.parentId() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goalStream, GoalFilterDto filterDto) {
        return goalStream.filter(goal -> goal.getParent().getId().equals(filterDto.parentId()));
    }
}

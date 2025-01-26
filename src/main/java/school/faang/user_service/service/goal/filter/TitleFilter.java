package school.faang.user_service.service.goal.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.filter.filterI.GoalFilter;

import java.util.stream.Stream;

@Component
public class TitleFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto filterDto) {
        return filterDto.title() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goalStream, GoalFilterDto filterDto) {
        return goalStream.filter(goal -> goal.getTitle().equals(filterDto.title()));
    }
}

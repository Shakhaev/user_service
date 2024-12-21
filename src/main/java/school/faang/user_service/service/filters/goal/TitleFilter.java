package school.faang.user_service.service.filters.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component
public class TitleFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto filters) {
        return filters.getTitle() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filters) {
        String regex = ".*" + Pattern.quote(filters.getTitle()) + ".*";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        return goals
                .filter(goal -> pattern.matcher(goal.getTitle()).matches());
    }
}
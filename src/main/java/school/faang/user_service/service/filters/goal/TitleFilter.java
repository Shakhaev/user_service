package school.faang.user_service.service.filters.goal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
@Configuration
public class TitleFilter implements GoalFilter {
    @Override
    public boolean isApplicable(GoalFilterDto filters) {
        boolean applicable = filters.getTitle() != null;
        log.info("TitleFilter is applicable");
        return applicable;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filters) {
        String regex = ".*" + Pattern.quote(filters.getTitle()) + ".*";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    //    log.info("Applying title filter with regex: {}", regex);
            log.info("Applying title filter with regex");

        return goals
                .filter(goal -> {
                    boolean matches = pattern.matcher(goal.getTitle()).matches();
              //      log.info("Goal title: {}, matches: {}", goal.getTitle(), matches);
                    log.info("Goal title, matches");
                    return matches;
                });
    }
}
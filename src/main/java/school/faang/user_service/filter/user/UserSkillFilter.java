package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserSkillFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters != null && filters.getSkillPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        if (users == null || filters == null || filters.getSkillPattern() == null) {
            return Stream.empty();
        }

        return users.filter(user -> user.getSkills() != null && user.getSkills().stream()
                .anyMatch(skill -> skill.getTitle() != null && skill.getTitle()
                        .toUpperCase()
                        .contains(filters.getSkillPattern().toUpperCase()))
        );
    }
}

package school.faang.user_service.filter.subscriber;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class UserSkillFilter implements SubscriberFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getSkillPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(user -> user.getSkills() != null
                && user.getSkills().stream()
                .anyMatch(skill -> skill.getTitle() != null
                        && skill.getTitle().contains(filters.getSkillPattern())));
    }
}
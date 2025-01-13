package school.faang.user_service.service.filters.user;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserSkillFilter extends UserFilter {

    @Override
    public Object getFilterFieldValue(UserFilterDto filters) {
        return filters.getSkillPattern();
    }

    @Override
    public Stream<User> apply(@NotNull Stream<User> users, UserFilterDto filters) {
        return users.filter(user -> user.getSkills().stream()
                .anyMatch(skill ->
                        skill.getTitle().contains(filters.getSkillPattern())));
    }
}

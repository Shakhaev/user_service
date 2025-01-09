package school.faang.user_service.service.filters.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserExperienceMaxFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getExperienceMax() != null;
    }

    @Override
    public void apply(Stream<User> users, UserFilterDto filters) {
        users.filter(user ->
                user.getExperience() <= filters.getExperienceMax());
    }
}

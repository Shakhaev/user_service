package school.faang.user_service.filters.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.user.User;

import java.util.stream.Stream;

@Component
public class UserNameFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getNamePattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(user -> user.getUsername().contains(filters.getNamePattern()));
    }
}

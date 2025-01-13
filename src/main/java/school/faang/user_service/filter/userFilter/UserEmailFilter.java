package school.faang.user_service.filter.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserEmailFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters != null && filters.getEmailPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        if (!validateParameters(users, filters)) {
            return Stream.empty();
        }

        return users.filter(user -> user.getEmail() != null && user.getEmail()
                .toUpperCase().contains(filters.getEmailPattern().toUpperCase()));
    }
}

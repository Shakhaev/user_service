package school.faang.user_service.service.goal.filter.subscription;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.goal.filter.fiilterabs.UserFilter;

import java.util.stream.Stream;

public class EmailFilter implements UserFilter {
    @Override
    public boolean isAcceptable(UserFilterDto userFilterDto) {
        return userFilterDto.emailPattern() != null;
    }

    @Override
    public Stream<User> accept(Stream<User> users, UserFilterDto userFilterDto) {
        return users.filter(user -> matchesPattern(userFilterDto.emailPattern(), user.getEmail()));
    }

    private boolean matchesPattern(String pattern, String value) {
        return pattern == null || value.matches(pattern);
    }
}

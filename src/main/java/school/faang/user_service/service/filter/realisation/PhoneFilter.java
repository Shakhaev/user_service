package school.faang.user_service.service.filter.realisation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.filter.UserFilter;

import java.util.stream.Stream;

@Component
public class PhoneFilter implements UserFilter {
    @Override
    public boolean isAcceptable(UserFilterDto userFilterDto) {
        return userFilterDto.phonePattern() != null;
    }

    @Override
    public Stream<User> accept(Stream<User> users, UserFilterDto userFilterDto) {
        return users.filter(user -> matchesPattern(userFilterDto.phonePattern(), user.getPhone()));
    }

    private boolean matchesPattern(String pattern, String value) {
        return pattern == null || value.matches(pattern);
    }
}
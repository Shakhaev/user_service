package school.faang.user_service.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.entity.User;

import java.util.stream.Stream;

public class NamePatternFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        return filterDto.getNamePattern() != null;
    }

    @Override
    public Stream <User> apply(Stream<User> users, UserFilterDto filterDto) {
       return users.filter(user -> user.getUsername().matches(filterDto.getNamePattern()));
    }
}

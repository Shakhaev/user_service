package school.faang.user_service.filters.user.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.user.UserFilter;

import java.util.stream.Stream;

@Component
public class UserNameFilter implements UserFilter {

    @Override
    public Stream<User> apply(Stream<User> stream, UserFilterDto dto) {
        if (dto.getName() == null) {
            return stream;
        }
        return stream.filter(user -> user.getUsername().contains(dto.getName()));
    }
}

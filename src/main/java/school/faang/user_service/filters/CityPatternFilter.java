package school.faang.user_service.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.entity.User;
import java.util.stream.Stream;

public class CityPatternFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        return filterDto.getCityPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filterDto) {
        return users.filter(user -> user.getCity().matches(filterDto.getCityPattern()));
    }
}

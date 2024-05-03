package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserCountryFilter implements UserFilter {

    @Override
    public Stream<User> applyFilter(Stream<User> users, UserFilterDto userFilterDto) {
        if (userFilterDto.getCountryPattern() != null) {
            return users.filter(user -> user.getCountry().getTitle().startsWith(userFilterDto.getAboutPattern()));
        }
        return users;
    }
}

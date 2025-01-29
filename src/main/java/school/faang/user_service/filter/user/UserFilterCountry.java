package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class UserFilterCountry implements UserFilter {

    @Override
    public boolean apply(User user, UserFilterDto userFilterDto) {
        return userFilterDto.getCountryPattern() == null
                || user.getCountry().getTitle().contains(userFilterDto.getCountryPattern());
    }
}
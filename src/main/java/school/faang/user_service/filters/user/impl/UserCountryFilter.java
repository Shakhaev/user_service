package school.faang.user_service.filters.user.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.user.UserFilter;

@Component
public class UserCountryFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getCountryPattern() != null;
    }

    @Override
    public boolean filterEntity(User user, UserFilterDto filters) {
        return user.getCountry().getTitle().contains(filters.getCountryPattern());
    }
}

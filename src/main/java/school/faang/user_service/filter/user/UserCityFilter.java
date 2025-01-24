package school.faang.user_service.filter.user;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class UserCityFilter extends UserFilter {

    @Override
    public Object getFilterFieldValue(UserFilterDto filters) {
        return filters.getCityPattern();
    }

    @Override
    public boolean apply(User user, UserFilterDto filters) {
        return StringUtils.contains(user.getCity(),
                filters.getCityPattern());
    }
}

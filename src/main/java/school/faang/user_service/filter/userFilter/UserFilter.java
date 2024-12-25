package school.faang.user_service.filter.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.model.User;

@Component
public abstract class UserFilter {
    protected String pattern;

    public abstract boolean isApplicable(UserFilterDto filters);
    public abstract boolean apply(User user);
}

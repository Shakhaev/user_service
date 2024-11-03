package school.faang.user_service.filter.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class NamePatternFilter implements UserFilter {
    private String pattern;

    @Override
    public boolean isApplicable(UserFilterDto filters) {
        this.pattern = filters.namePattern();
        return pattern != null;
    }

    @Override
    public boolean apply(User user) {
        return user.getUsername() != null && user.getUsername().contains(pattern);
    }
}
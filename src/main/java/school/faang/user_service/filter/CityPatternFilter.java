package school.faang.user_service.filter;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

public class CityPatternFilter implements SubscriptionFilter {
    private String pattern;

    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        this.pattern = filterDto.cityPattern();
        return pattern != null && !pattern.isEmpty();
    }

    @Override
    public boolean apply(User user) {
        return user.getCity() != null && user.getCity().contains(pattern);
    }
}

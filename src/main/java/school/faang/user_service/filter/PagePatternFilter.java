package school.faang.user_service.filter;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

public class PagePatternFilter implements SubscriptionFilter {
    private int pattern;

    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        this.pattern = filterDto.page();
        return pattern > 0;
    }

    @Override
    public boolean apply(User user) {
        return true;
    }
}

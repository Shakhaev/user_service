package school.faang.user_service.filter;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

public class ContactPatternFilter implements SubscriptionFilter {
    private String pattern;

    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        this.pattern = filterDto.contactPattern();
        return pattern != null && !pattern.isEmpty();
    }

    @Override
    public boolean apply(User user) {
        return user.getContacts() != null && user.getContacts().contains(pattern);
    }
}

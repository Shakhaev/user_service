package school.faang.user_service.service.filters;

import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class EmailPatternFilter implements UserFilter {
    @Override
    public boolean isApplicable(SubscriptionUserFilterDto filters) {
        return filters.getEmailPattern() != null;
    }

    @Override
    public void apply(Stream<User> users, SubscriptionUserFilterDto subscriptionUserFilterDto) {
        users.filter(user -> user.getEmail().matches(subscriptionUserFilterDto.getEmailPattern()));
    }
}

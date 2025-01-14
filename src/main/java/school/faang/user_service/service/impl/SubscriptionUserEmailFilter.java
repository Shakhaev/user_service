package school.faang.user_service.service.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.SubscriptionFilter;

import java.util.stream.Stream;

@Component
public class SubscriptionUserEmailFilter implements SubscriptionFilter {
    @Override
    public boolean isApplicable(SubscriptionUserFilterDto filter) {
        return filter.emailPattern() != null && !filter.emailPattern().isEmpty();
    }

    @Override
    public Stream<User> apply(Stream<User> users, SubscriptionUserFilterDto filter) {
        return users.filter(user -> user.getEmail().contains(filter.emailPattern()));
    }
}

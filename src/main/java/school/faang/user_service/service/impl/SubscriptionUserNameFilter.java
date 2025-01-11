package school.faang.user_service.service.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.SubscriptionFilter;

import java.util.stream.Stream;

@Component
public class SubscriptionUserNameFilter implements SubscriptionFilter {
    @Override
    public boolean isApplicable(SubscriptionUserFilterDto filter) {
        return filter.getNamePattern() != null;
    }

    @Override
    public void apply(Stream<User> users, SubscriptionUserFilterDto filter) {
        users.filter(user -> user.getUsername().matches(filter.getNamePattern()));
    }
}

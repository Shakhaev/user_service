package school.faang.user_service.service.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.SubscriptionFilter;

import java.util.stream.Stream;

@Component
public class SubscriptionUserDefaultFilter implements SubscriptionFilter {
    @Override
    public String getName() {
        return "User User Default Filter";
    }
    @Override
    public boolean isApplicable(SubscriptionUserFilterDto filter) {
        return true;
    }

    @Override
    public Stream<User> apply(Stream<User> users, SubscriptionUserFilterDto filter) {
        return users.filter(user -> user.getId() > 0);
    }
}

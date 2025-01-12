package school.faang.user_service.service.impl;

import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.SubscriptionFilter;

import java.util.stream.Stream;

public class SubscriptionUserCountryFilter implements SubscriptionFilter {

    @Override
    public boolean isApplicable(SubscriptionUserFilterDto filter) {
        return filter.getCountryPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, SubscriptionUserFilterDto filter) {
        return users.filter(user -> user.getCountry().getTitle().matches(filter.getCountryPattern()));
    }
}

package school.faang.user_service.service.impl;

import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.SubscriptionFilter;

import java.util.stream.Stream;

@Component
public class SubscriptionUserAboutFilter implements SubscriptionFilter {
    @Override
    public String getName() {
        return "User About Filter";
    }

    @Override
    public boolean isApplicable(SubscriptionUserFilterDto filter) {
        return !StringUtils.isBlank(filter.aboutPattern());
    }

    @Override
    public Stream<User> apply(Stream<User> users, SubscriptionUserFilterDto filter) {
        return users.filter(user -> user.getAboutMe().contains(filter.aboutPattern()));
    }
}

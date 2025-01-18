package school.faang.user_service.service.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.SubscriptionFilter;

import java.util.stream.Stream;

@Component
public class SubscriptionUserExperienceFilter implements SubscriptionFilter {
    @Override
    public boolean isApplicable(SubscriptionUserFilterDto filter) {
        int minExperience = filter.experienceMin();
        int maxExperience = filter.experienceMax();
        return minExperience > 0 && maxExperience > 0 && minExperience < maxExperience;
    }

    @Override
    public Stream<User> apply(Stream<User> users, SubscriptionUserFilterDto filter) {
        return users.filter(user -> (user.getExperience() >= filter.experienceMin()
                && user.getExperience() < filter.experienceMax()));
    }
}

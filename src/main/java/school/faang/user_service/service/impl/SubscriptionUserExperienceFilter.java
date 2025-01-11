package school.faang.user_service.service.impl;

import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.SubscriptionFilter;

import java.util.stream.Stream;

public class SubscriptionUserExperienceFilter implements SubscriptionFilter {
    @Override
    public boolean isApplicable(SubscriptionUserFilterDto filter) {
        int minExperience = filter.getExperienceMin();
        int maxExperience = filter.getExperienceMax();
        return minExperience > 0 && maxExperience > 0 && minExperience < maxExperience;
    }

    @Override
    public void apply(Stream<User> users, SubscriptionUserFilterDto filter) {
        users.filter(user -> (user.getExperience() >= filter.getExperienceMin()
                && user.getExperience() < filter.getExperienceMax()));
    }
}

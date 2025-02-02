package school.faang.user_service.filter.subscriber;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class UserExperienceFilter implements SubscriberFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getExperienceMin() > 0 || filters.getExperienceMax() > 0;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        int minExperience = filters.getExperienceMin();
        int maxExperience = filters.getExperienceMax();

        return users.filter(user -> user.getExperience() != null
                && user.getExperience() >= minExperience
                && user.getExperience() <= maxExperience);
    }
}
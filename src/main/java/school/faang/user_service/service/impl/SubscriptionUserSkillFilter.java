package school.faang.user_service.service.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.SubscriptionFilter;

import java.util.stream.Stream;

@Component
public class SubscriptionUserSkillFilter implements SubscriptionFilter {
    @Override
    public boolean isApplicable(SubscriptionUserFilterDto filter) {
        String skillPattern = filter.skillPattern();
        return (skillPattern != null && !skillPattern.isEmpty());
    }

    @Override
    public Stream<User> apply(Stream<User> users, SubscriptionUserFilterDto filter) {
        return users.filter(user -> user.getSkills().stream()
                .anyMatch(s -> s.getTitle().matches(filter.skillPattern())));
    }
}

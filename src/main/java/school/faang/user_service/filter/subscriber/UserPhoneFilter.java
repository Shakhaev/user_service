package school.faang.user_service.filter.subscriber;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class UserPhoneFilter implements SubscriberFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getPhonePattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(user -> user.getPhone() != null
                && user.getPhone().contains(filters.getPhonePattern()));
    }
}
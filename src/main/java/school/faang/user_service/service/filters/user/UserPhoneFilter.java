package school.faang.user_service.service.filters.user;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserPhoneFilter extends UserFilter {

    @Override
    public Object getFilterFieldValue(UserFilterDto filters) {
        return filters.getPhonePattern();
    }

    @Override
    public Stream<User> apply(@NotNull Stream<User> users, UserFilterDto filters) {
        return users.filter(user ->
                user.getPhone().contains(filters.getPhonePattern()));
    }
}

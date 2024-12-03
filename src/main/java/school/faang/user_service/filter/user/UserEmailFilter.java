package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

@Component
public class UserEmailFilter implements Filter<User, UserFilterDto> {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getEmail() != null && !filter.getEmail().isEmpty();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        String emailPattern = filter.getEmail().toLowerCase();
        return users.filter(user -> user.getEmail() != null &&
                user.getEmail().toLowerCase().contains(emailPattern));
    }
}

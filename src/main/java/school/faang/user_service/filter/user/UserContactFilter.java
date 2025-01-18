package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserContactFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getContactPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(user -> user.getContacts()
                    .stream()
                    .anyMatch(contact -> filters.getContactPattern().equals(contact.getContact())));
    }
}

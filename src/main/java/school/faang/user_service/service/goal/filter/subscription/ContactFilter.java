package school.faang.user_service.service.goal.filter.subscription;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.goal.filter.filterI.UserFilter;

import java.util.stream.Stream;

public class ContactFilter implements UserFilter {
    @Override
    public boolean isAcceptable(UserFilterDto userFilterDto) {
        return userFilterDto.contactPattern() != null;
    }

    @Override
    public Stream<User> accept(Stream<User> users, UserFilterDto userFilterDto) {
        return users.filter((user -> userFilterDto.contactPattern() == null
                || user.getContacts().stream()
                .allMatch(contact -> matchesPattern(userFilterDto.contactPattern(), contact.getContact()))));
    }

    private boolean matchesPattern(String pattern, String value) {
        return pattern == null || value.matches(pattern);
    }
}

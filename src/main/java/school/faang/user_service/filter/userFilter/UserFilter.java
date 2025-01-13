package school.faang.user_service.filter.userFilter;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface UserFilter {
    boolean isApplicable(UserFilterDto filters);

    Stream<User> apply(Stream<User> users, UserFilterDto filters);
    default boolean validateParameters(Stream<User> users, UserFilterDto filters) {
        return users != null && filters != null;
    }
}

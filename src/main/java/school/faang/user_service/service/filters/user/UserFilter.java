package school.faang.user_service.service.filters.user;

import lombok.NonNull;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public abstract class UserFilter {
    public boolean isApplicable(UserFilterDto filters) {
        return filters != null && getFilterFieldValue(filters) != null;
    }

    public abstract Object getFilterFieldValue(UserFilterDto filters);

    public abstract Stream<User> apply(@NonNull Stream<User> users, UserFilterDto filters);
}

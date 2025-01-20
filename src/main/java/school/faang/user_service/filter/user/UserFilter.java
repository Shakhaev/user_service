package school.faang.user_service.filter.user;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface UserFilter {
    boolean isApplicable(UserFilterDto presetDto);

    Stream<User> apply(Stream<User> users, UserFilterDto presetDto);
}

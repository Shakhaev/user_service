package school.faang.user_service.filter.user;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class UserFilterByNamePattern implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto presetDto) {
        return presetDto.getUsernamePattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto presetDto) {
        return users.filter(user -> user.getUsername().contains(presetDto.getUsernamePattern()));
    }
}

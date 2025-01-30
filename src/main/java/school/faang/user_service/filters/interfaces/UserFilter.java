package school.faang.user_service.filters.interfaces;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface UserFilter {
    boolean isAcceptable(UserFilterDto userFilterDto);

    Stream<User> accept(Stream<User> users, UserFilterDto userFilterDto);
}
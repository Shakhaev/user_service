package school.faang.user_service.service;

import java.util.List;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserDto;

public interface UserService {
    UserDto getUser(long userId);

    List<UserDto> getUsersByIds(List<Long> ids);

    UserDto createUser(UserCreateDto userCreateDto);
}

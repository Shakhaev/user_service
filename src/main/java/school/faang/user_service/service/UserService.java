package school.faang.user_service.service;

import school.faang.user_service.dto.user.UserDto;

public interface UserService {
    UserDto getUser(long userId);
}

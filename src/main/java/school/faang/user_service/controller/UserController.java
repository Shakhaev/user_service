package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    public UserDto createUser(UserDto userDto) {
        return userService.createUser(userDto);
    }
}

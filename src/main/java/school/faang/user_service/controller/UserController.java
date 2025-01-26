package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.UserService;

@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    public UserDto createUser(UserDto userDto) {
        if (userDto.username() == null || userDto.username().isBlank()) {
            throw new DataValidationException("username can't be blank");
        }
        return userService.createUser(userDto);
    }
}

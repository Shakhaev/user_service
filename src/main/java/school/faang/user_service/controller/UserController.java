package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.UserValidator;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserValidator userValidation;

    public UserDto deactivateUser(long userId) {
        userValidation.validateUserId(userId);
        return userService.deactivate(userId);
        }
    public List<UserDto> getPremiumUsers(UserFilterDto filter) {
        return userService.getPremiumUsers(filter);
    }
}

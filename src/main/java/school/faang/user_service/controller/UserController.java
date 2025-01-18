package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validation.UserValidation;

@Controller
public class UserController {

    private final UserService userService;
    private final UserValidation userValidation;

    @Autowired
    public UserController(UserService userService, UserValidation userValidation) {
        this.userService = userService;
        this.userValidation = userValidation;
    }

    public UserDto deactivateUser(long userId) {
        userValidation.validateUserId(userId);
        UserDto user = userService.deactivate(userId);
        userService.removeMenteeAndGoals(userId);
        return user;
    }
}
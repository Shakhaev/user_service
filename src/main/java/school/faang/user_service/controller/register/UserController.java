package school.faang.user_service.controller.register;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.register.UserProfileDto;
import school.faang.user_service.dto.register.UserRegistrationDto;
import school.faang.user_service.service.user.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public UserProfileDto registerUser(@RequestBody UserRegistrationDto registrationDto,
                                       @RequestHeader(value = "x-user-id", required = false) String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID is missing. Please make sure 'x-user-id' header is included in the request.");
        }
        return userService.registerUser(registrationDto);
    }
}

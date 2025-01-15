package school.faang.user_service.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    public void deactivateUser(@RequestParam @NotNull Long userId) {
        userService.deactivateUser(userId);
    }
}

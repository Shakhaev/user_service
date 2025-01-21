package school.faang.user_service.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.BooleanResponse;
import school.faang.user_service.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public void deactivateUser(@RequestParam @NotNull Long userId) {
        userService.deactivateUser(userId);
    }

    @GetMapping("/is-user-exists")
    public BooleanResponse isUserExist(@RequestParam(name = "user_id") Long userId) {
        return new BooleanResponse(userService.isUserExist(userId));
    }
}

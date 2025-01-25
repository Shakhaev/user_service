package school.faang.user_service.controller.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.user.UserService;

@RequiredArgsConstructor
@Validated
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/user/exists/{userId}")
    public ResponseEntity<Boolean> userExists(@PathVariable @Positive long userId) {
        boolean response = userService.userExists(userId);
        return ResponseEntity.ok(response);
    }
}

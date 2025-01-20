package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.service.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    public void deactivateUser(Long userId) {
        service.deactivateUser(userId);
    }
}

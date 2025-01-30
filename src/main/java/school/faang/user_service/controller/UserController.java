package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.UserReadDto;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserReadDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PostMapping("/list")
    List<UserReadDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);

    public List<UserDto> getPremiumUsers(UserFilterDto filter) {
        return userService.getPremiumUsers(filter);
    }
}

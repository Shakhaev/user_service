package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.implementations.UserServiceImpl;
import school.faang.user_service.validation.ValidationController;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserServiceImpl userServiceImpl;
    private final ValidationController validationController;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        validationController.validateIdCorrect(userId);
        return userServiceImpl.getUser(userId);
    }

    @PostMapping("/get")
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        validationController.validateListIdsCorrect(ids);
        return userServiceImpl.getUsersByIds(ids);
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserCreateDto userCreateDto) {
        return userServiceImpl.createUser(userCreateDto);
    }

    @GetMapping("/avatar")
    public void downloadAvatar() {
        userServiceImpl.addAvatarToUser();
    }
}
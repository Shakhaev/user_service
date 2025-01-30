package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.UserRegisterRequest;
import school.faang.user_service.dto.UserRegisterResponse;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    public void deactivateUser(Long userId) {
        userService.deactivateUser(userId);
    }

    @PostMapping("/register")
    public UserRegisterResponse register(@Valid @RequestBody UserRegisterRequest request) {
        return userService.register(request);
    }

    @GetMapping("/{userId}/avatar")
    public ResponseEntity<byte[]> getUserAvatar(@Valid @NotNull @Positive @PathVariable Long userId) {
        byte[] avatarBytes = userService.getUserAvatar(userId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/svg+xml"))
                .body(avatarBytes);
    }

    @PostMapping("/premium")
    public List<UserDto> getPremiumUsers(@RequestBody UserFilterDto filterDto) {
        return userService.getPremiumUsers(filterDto);
    }

    @GetMapping("/users/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PostMapping("/users")
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }
}

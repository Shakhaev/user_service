package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserRegisterRequest;
import school.faang.user_service.dto.UserRegisterResponse;
import school.faang.user_service.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    public void deactivateUser(Long userId) {
        userService.deactivateUser(userId);
    }

    @PostMapping("/user/register")
    public UserRegisterResponse register(@Valid @RequestBody UserRegisterRequest request) {
        return userService.register(request);
    }

    @GetMapping("/user/{userId}/avatar")
    public ResponseEntity<byte[]> getUserAvatar(@Valid @NotNull @Positive @PathVariable Long userId) {
        byte[] avatarBytes = userService.getUserAvatar(userId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/svg+xml"))
                .body(avatarBytes);
    }
}

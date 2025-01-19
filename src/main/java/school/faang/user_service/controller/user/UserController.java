package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.annotation.publisher.PublishEvent;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.service.user.UserDomainService;
import school.faang.user_service.service.user.UserService;

import java.util.List;

import static school.faang.user_service.enums.publisher.PublisherType.PROFILE_VIEW;

@Slf4j
@Tag(name = "User Management", description = "Operations related to user management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserDomainService userDomainService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "Register a new user", description = "Registers a new user and returns the created user data.")
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        User registeredUser = userService.registerUser(user);
        UserDto responseDto = userMapper.toUserDto(registeredUser);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Deactivate a user", description = "Deactivates a user by their ID.")
    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivatedUser(@PathVariable Long userId) {
        userService.deactivateUser(userId);

        return ResponseEntity.noContent().build();
    }

    @PublishEvent(type = PROFILE_VIEW)
    @PostMapping("/search/premium")
    public List<UserResponseDto> getPremiumUsers(@RequestParam long offset, @RequestParam long limit) {
        List<User> foundUsers = userService.getPremiumUsers(offset, limit);
        return userMapper.toDtos(foundUsers);
    }

    @PublishEvent(type = PROFILE_VIEW)
    @GetMapping("/{userId}")
    public UserResponseDto getUser(@PathVariable long userId) {
        User user = userDomainService.findById(userId);
        return userMapper.toDto(user);
    }

    @PublishEvent(type = PROFILE_VIEW)
    @PostMapping()
    List<UserResponseDto> getUsersByIds(@RequestBody List<Long> ids) {
        List<User> users = userDomainService.findAllByIds(ids);
        return userMapper.toDtos(users);
    }

    @PostMapping("/active")
    public List<Long> getOnlyActiveUsersFromList(@RequestBody List<Long> ids) {
        return userDomainService.getOnlyActiveUserIdsFromList(ids);
    }
}

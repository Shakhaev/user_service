package school.faang.user_service.controller.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/user/{userId}")
    ResponseEntity<UserDto> getUser(@PathVariable @Positive long userId) {
        User user = userService.getUser(userId);
        UserDto userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/users")
    ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody List<UserDto> users) {
        List<User> userList = userMapper.toUserList(users);
        userList = userService.getUsersByIds(userList);
        List<UserDto> userDtoList = userMapper.toUserDtoList(userList);
        return ResponseEntity.ok(userDtoList);
    }

    @DeleteMapping("/user/deactivate")
    public void deactivateUser(@RequestParam("userId") Long userId) {
        userService.deactivateUser(userId);
    }
  
    @GetMapping("/user/exists/{userId}")
    public ResponseEntity<Boolean> userExists(@PathVariable @Positive long userId) {
        boolean response = userService.userExists(userId);
        return ResponseEntity.ok(response);
    }
}

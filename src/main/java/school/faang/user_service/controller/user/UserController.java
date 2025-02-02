package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.DeactivatedUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/premium")
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        if (userFilterDto == null){
            return userService.getPremiumUsers();
        }
        return userService.getPremiumUsers(userFilterDto);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<DeactivatedUserDto> deactivateUser(@PathVariable long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }
}

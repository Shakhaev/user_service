package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserNFDto;
import school.faang.user_service.dto.user.UserResponseCsvDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "User API", description = "API for managing users")
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    @GetMapping("/users/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.getUserDtoByID(userId);
    }

    @GetMapping("/users/{userId}/nf")
    public UserNFDto getUserNF(@PathVariable long userId) {
        return userService.getUserNFDtoByID(userId);
    }

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/filtered")
    public List<UserDto> getFilteredUsers(@RequestBody UserFilterDto filterDto) {
        return userService.findByFilter(filterDto);
    }

    @GetMapping("/users/subscribers/{userId}")
    public List<UserDto> getUserSubscribers(@PathVariable long userId) {
        return userService.getUserSubscribers(userId);
    }

    @GetMapping("/users/followers/{userId}/nf")
    public List<UserNFDto> getUserFollowers(@PathVariable long userId) {
        return userService.getUserFollowers(userId);
    }

    @PostMapping("/CSV")
    public List<UserResponseCsvDto> getUsersFromCsv(@RequestParam MultipartFile file) {
        return userService.readingUsersFromCsv(file);
    }
}
package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.request.UsersDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userMapper.toDto(userService.findUser(userId)));
    }

    @PostMapping
    public ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody UsersDto ids) {
        return ResponseEntity.ok(userService.getUsersByIds(ids));
    }

    /**
     * Gets all users with optional filtering.
     *
     * @param filter DTO with filtering parameters
     * @return list of UserDTOs of all users
     */
    @Operation(
            summary = "Get all users with optional filtering",
            description = "Returns a list of all users with optional filtering",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content
                    )
            }
    )
    @GetMapping
    public List<UserDto> getAllUsers(
            @Valid
            @Parameter(description = "All users filtration parameters", required = false)
            @ModelAttribute UserFilterDto filter) {
        return userService.getAllUsers(filter);
    }

    /**
     * Gets a list of premium users with optional filtering
     *
     * @param filter DTO with filtering parameters
     * @return list of UserDTOs of premium users
     */
    @Operation(
            summary = "Get premium users",
            description = "Returns list of premium users with filtration options",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content
                    )

            }
    )
    @GetMapping("/premium")
    public List<UserDto> getPremiumUsers(
            @Valid
            @Parameter(description = "Premium users filtration parameters", required = false)
            @ModelAttribute UserFilterDto filter) {
        return userService.getPremiumUsers(filter);
    }
}

package school.faang.user_service.controller;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.dto.UserSubResponseDto;
import school.faang.user_service.dto.user.DeactivatedUserDto;
import school.faang.user_service.dto.user.UserForNotificationDto;
import school.faang.user_service.message.event.ProfileViewEvent;
import school.faang.user_service.service.user.UserDeactivationService;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserV1Controller {
    private final UserDeactivationService userDeactivationService;
    private final UserService userService;
    private final UserContext userContext;

    @GetMapping("/{userId}")
    public UserSubResponseDto getUser(@Positive @PathVariable long userId) {
        UserSubResponseDto userSubResponseDto = userService.getUserDtoById(userId);

        long viewerUserId = userContext.getUserId();
        ProfileViewEvent profileViewEvent = new ProfileViewEvent(userId, viewerUserId);
        userService.publishProfileViewEvent(profileViewEvent);

        return userSubResponseDto;
    }

    @GetMapping("/notification/{userId}")
    public UserForNotificationDto getUserByIdForNotification(@Positive @PathVariable long userId) {
        return userService.getUserByIdForNotification(userId);
    }

    @PostMapping("/get")
    public List<UserSubResponseDto> getUsersByIds(@NotEmpty @RequestBody List<@Positive Long> ids) {
        return userService.getAllUsersDtoByIds(ids);
    }

    @PutMapping("/{userId}/avatar")
    @ResponseStatus(HttpStatus.OK)
    public UserProfilePicDto updateAvatar(@PathVariable("userId") @Positive long userId,
                                          @RequestPart MultipartFile file) {
        return userService.updateUserProfilePicture(userId, file);
    }

    @GetMapping(value = "/{userId}/avatar", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public InputStreamResource getAvatar(@PathVariable("userId") @Positive long userId) {
        return userService.getUserAvatar(userId);
    }

    @DeleteMapping("/{userId}/avatar")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAvatar(@PathVariable("userId") @Positive long userId) {
        userService.deleteUserAvatar(userId);
    }

    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<DeactivatedUserDto> deactivateUser(@PathVariable @NotNull @Positive long userId) {
        DeactivatedUserDto deactivatedUser = userDeactivationService.deactivateUser(userId);
        return ResponseEntity.ok(deactivatedUser);
    }

    @PostMapping("/premium")
    public List<UserSubResponseDto> getPremiumUsers(@RequestBody UserFilterDto userFilterDto) {
        return userService.getPremiumUsers(userFilterDto);
    }
}
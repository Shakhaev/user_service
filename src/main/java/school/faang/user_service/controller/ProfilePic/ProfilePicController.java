package school.faang.user_service.controller.ProfilePic;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.avatar.UserProfilePicDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.avatar.ProfilePicServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.default}/pic")
public class ProfilePicController {
    private final ProfilePicServiceImpl profilePicService;

    @Value("${services.s3.maxSizeBytes}")
    private int maxSizeBytes;

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public @Valid UserProfilePicDto saveProfilePic(@Positive @PathVariable long userId, @NotEmpty @RequestParam("file") MultipartFile file) {
        if (file.getSize() > maxSizeBytes) {
            throw new DataValidationException("The maximum file size of 5 MB has been exceeded");
        }
        return profilePicService.saveProfilePic(userId, file);
    }

    @GetMapping("/{userId}")
    public InputStreamResource getProfilePic(@Positive @PathVariable long userId) {
        return profilePicService.getProfilePic(userId);
    }

    @DeleteMapping("/{userId}")
    public String deleteProfilePic(@Positive @PathVariable long userId) {
        return profilePicService.deleteProfilePic(userId);
    }
}

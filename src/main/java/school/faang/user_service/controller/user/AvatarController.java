package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.user.AvatarService;

@RestController
@RequiredArgsConstructor
public class AvatarController {
    private final AvatarService avatarService;

//    @GetMapping("/avatar")
//    public Object getAvatar() {
//        return avatarService.generateAvatarAndSaveToMinio();
//    }

    @PostMapping("/upload")
    public void uploadFileToMinio(@RequestPart(value = "avatar") MultipartFile multipartFile) {
        avatarService.uploadAvatar(multipartFile);
    }
}

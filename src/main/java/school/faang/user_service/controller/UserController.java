package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.UserService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @Value("${services.s3.fileLimit}")
    private int FILE_LIMIT;

    @GetMapping("/users/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return userService.findUserById(userId);
    }

    @PostMapping("/users")
    public List<UserDto> getUsersByIds(@RequestBody List<Long> userIds) {
        return userService.findUsersByIds(userIds);
    }

    @PostMapping("usersPic/{userId}")
    public UserProfilePicDto addUsersPic(@PathVariable long userId, @RequestBody MultipartFile file) throws IOException {

        if (file.getSize() > FILE_LIMIT) {
            throw new FileSizeLimitExceededException("File Size Limit ", file.getSize(), FILE_LIMIT);
        }
        return userService.addUserPic(userId, file);
    }

    @GetMapping("usersPic/{userId}")
    public ResponseEntity<byte[]> getUserPic(@PathVariable long userId) throws IOException {
        byte[] imageBytes = null;
        try {
            imageBytes = userService.getUserPic(userId).readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @DeleteMapping("usersPic/{userId}")
    public void deleteUserPic(@PathVariable long userId) {
        userService.deleteUserPic(userId);
    }
}

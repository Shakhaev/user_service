package school.faang.user_service.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.config.dicebear.AvatarType;
import school.faang.user_service.service.external.DiceBearService;
import school.faang.user_service.service.external.S3Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Validated
@Service
public class UserAvatarService {
    private final S3Service s3Service;
    private final DiceBearService diceBearService;
    @Value("${avatar.bucketName}")
    private String bucketName;

    public User generateRandomAvatar(@NotNull User user, AvatarType type) {
        String randomName = UUID.randomUUID().toString();
        byte[] avatarData = diceBearService.generateAvatar(randomName, type);
        String avatarNameAndExtension = randomName + type.getExtension();

        String fileId = s3Service.uploadToBucket(bucketName, avatarNameAndExtension, avatarData, type.getContentType());
        return setUploadedAvatar(user, fileId);
    }

    public String getUserAvatar(@NotNull User user) {
        String fileId = Optional.ofNullable(user.getUserProfilePic())
                .map(UserProfilePic::getFileId)
                .filter(id -> !id.isBlank())
                .orElseThrow(() -> new NoSuchElementException("No avatar for user " + user.getId()));

        return s3Service.getUnexpiredUrl(bucketName, fileId);
    }

    private User setUploadedAvatar(User user, String url) {
        UserProfilePic newPic = new UserProfilePic();
        newPic.setFileId(url);
        user.setUserProfilePic(newPic);
        return user;
    }
}

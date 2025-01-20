package school.faang.user_service.filters.avatar;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.service.user.AvatarService;

import java.awt.image.BufferedImage;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class SmallAvatarFilter implements AvatarFilter {
    private final AvatarService avatarService;

    @Value("${minio.avatar.resolution.small}")
    private int maxSize;

    @Override
    public void resizeAndUploadToMinio(BufferedImage originalImage,
                                       String formatName,
                                       UserProfilePic userProfilePic) {
        InputStream resizedImage = avatarService.resizeImage(originalImage, maxSize, formatName);
        String minioKey = avatarService.generateFileName(formatName);
        avatarService.uploadToMinio(resizedImage, minioKey);
        userProfilePic.setSmallFileId(minioKey);
    }
}

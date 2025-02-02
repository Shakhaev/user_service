package school.faang.user_service.filters.avatar;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.service.minio.ImageService;
import school.faang.user_service.service.minio.MinioService;

import java.awt.image.BufferedImage;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class BigAvatarFilter implements AvatarFilter {
    private final ImageService imageService;
    private final MinioService minioService;

    @Value("${minio.avatar.resolution.big}")
    private int maxSize;
    @Value("${minio.avatar.bucket}")
    private String bucketName;

    @Override
    public void resizeAndUploadToMinio(BufferedImage originalImage,
                                       String formatName,
                                       UserProfilePic userProfilePic) {
        InputStream resizedImage = imageService.resizeImage(originalImage, maxSize, formatName);
        String minioKey = imageService.generateImageName(formatName);
        minioService.upload(resizedImage, minioKey, bucketName);
        userProfilePic.setFileId(minioKey);
    }
}

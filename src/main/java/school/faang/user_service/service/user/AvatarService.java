package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.filters.avatar.AvatarFilter;
import school.faang.user_service.service.minio.ImageService;
import school.faang.user_service.service.minio.MinioService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Setter
@Service
@RequiredArgsConstructor
public class AvatarService {
    private final RestTemplate restTemplate;
    private final MinioService minioService;
    private final ImageService imageService;
    private final List<AvatarFilter> avatarFilters;

    @Value("${dicebear.api.url}")
    private String dicebearApiUrl;
    @Value("${minio.avatar.bucket}")
    private String bucketName;
    @Value("${minio.avatar.max-size}")
    private DataSize avatarMaxSize;

    public String generateRandomAvatar(String seed, String filename) {
        String url = UriComponentsBuilder.fromHttpUrl(dicebearApiUrl)
                .queryParam("seed", seed)
                .toUriString();

        String avatar = restTemplate.getForObject(url, String.class);
        if (avatar == null) {
            log.error("Avatar generation problem. Seed: {}; file name: {}.", seed, filename);
            throw new IllegalStateException("Could not generate an avatar");
        }

        InputStream inputStream = new ByteArrayInputStream(avatar.getBytes(StandardCharsets.UTF_8));
        return minioService.upload(inputStream, filename, bucketName);
    }

    public UserProfilePic uploadCustomAvatar(MultipartFile file) {
        try {
            String supportedFormatName = imageService.convertFromMimeType(file.getContentType());
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            UserProfilePic userProfilePic = new UserProfilePic();

            avatarFilters.forEach(avatarFilter ->
                    avatarFilter.resizeAndUploadToMinio(originalImage, supportedFormatName, userProfilePic));
            return userProfilePic;
        } catch (IOException e) {
            log.error("Error while converting a file to an image. MultipartFile: {}.", file, e);
            throw new RuntimeException(e);
        }
    }

    public void deleteAvatar(UserProfilePic userProfilePic) {
        minioService.delete(bucketName, userProfilePic.getFileId());
        minioService.delete(bucketName, userProfilePic.getSmallFileId());
    }

    public String getAvatar(UserProfilePic userProfilePic, boolean isSmall) {
        if (isSmall) {
            return minioService.getFileUrl(bucketName, userProfilePic.getSmallFileId());
        }
        return minioService.getFileUrl(bucketName, userProfilePic.getFileId());
    }

    public void checkUserHasAvatar(User user) {
        if (user.getUserProfilePic() == null) {
            log.error("Attempting to delete a non-existent avatar for a user with id={}.", user.getId());
            throw new RuntimeException("User doesn't have an avatar.");
        }
    }

    public void validateCustomAvatarSize(MultipartFile file) {
        if (file.getSize() > avatarMaxSize.toBytes()) {
            log.error("The avatar size exceeds the specified limit. File size: {}; max size: {}.",
                    file.getSize(), avatarMaxSize.toMegabytes());
            throw new IllegalArgumentException(
                    String.format("The image size should not exceed %s mb", avatarMaxSize.toMegabytes()));
        }
    }
}

package school.faang.user_service.service.user;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.actions.S3Actions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Setter
@Service
@RequiredArgsConstructor
public class AvatarService {
    private final RestTemplate restTemplate;
    private final AmazonS3 s3Client;

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
            throw new IllegalStateException("Could not generate an avatar");
        }
        return saveRandomGeneratedAvatar(avatar, filename);
    }

    public String saveRandomGeneratedAvatar(String svg, String fileName) {
        InputStream inputStream = new ByteArrayInputStream(svg.getBytes(StandardCharsets.UTF_8));
        return uploadToMinio(inputStream, fileName);

    }

    public String uploadToMinio(InputStream inputStream, String fileName) {
        try {
            log.debug("Start loading the avatar in minio.");
            if (!s3Client.doesBucketExistV2(bucketName)) {
                s3Client.createBucket(bucketName);
                String policyText = getPublicReadPolicy(bucketName);
                s3Client.setBucketPolicy(bucketName, policyText);
            }

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream, null)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save an avatar to minio", e);
        }

        log.debug("Successful completion of avatar upload in minio.");
        return s3Client.getUrl(bucketName, fileName).toString();
    }

    public InputStream resizeImage(BufferedImage originalImage, int maxDimension, String formatName) {
        try {
            BufferedImage resizedImage = Scalr.resize(originalImage,
                    Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, maxDimension);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, formatName, outputStream);

            log.debug("Successfully change the resolution of the image. Max size={}MB, Format name={}.",
                    avatarMaxSize.toMegabytes(), formatName);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error while changing the image resolution.", e);
        }
    }

    public void deleteFromMinio(UserProfilePic userProfilePic) {
        s3Client.deleteObject(bucketName, userProfilePic.getFileId());
        s3Client.deleteObject(bucketName, userProfilePic.getSmallFileId());
    }

    public String getAvatar(String fileName) {
        return s3Client.getUrl(bucketName, fileName).toString();
    }

    public void checkUserHasAvatar(User user) {
        if (user.getUserProfilePic() == null) {
            throw new RuntimeException("User doesn't have an avatar.");
        }
    }

    public String convertFromMimeType(String mimeType) {
        if (mimeType != null && mimeType.startsWith("image/")) {
            return mimeType.substring("image/".length());
        } else {
            throw new IllegalArgumentException("Unsupported content type: " + mimeType);
        }
    }

    public void validateCustomAvatarSize(MultipartFile file) {
        if (file.getSize() > avatarMaxSize.toBytes()) {
            throw new IllegalArgumentException(
                    String.format("The image size should not exceed %s mb", avatarMaxSize.toMegabytes()));
        }
    }

    public String generateFileName(String formatName) {
        return UUID.randomUUID() + "." + formatName;
    }

    private static String getPublicReadPolicy(String bucketName) {
        Policy bucketPolicy = new Policy().withStatements(
                new Statement(Statement.Effect.Allow)
                        .withPrincipals(Principal.AllUsers)
                        .withActions(S3Actions.GetObject)
                        .withResources(new Resource("arn:aws:s3:::" + bucketName + "/*")));
        return bucketPolicy.toJson();
    }
}

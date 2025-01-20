package school.faang.user_service.service.user;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvatarServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private AmazonS3 s3Client;
    @InjectMocks
    private AvatarService avatarService;

    @Value("${minio.avatarBucket}")
    private String bucketName = "test-bucket";
    @Value("${dicebear.api.url}")
    private String dicebearApiUrl = "http://localhost/dicebear";
    @Value("${minio.avatar.max-size}")
    private DataSize avatarMaxSize = DataSize.of(5, DataUnit.MEGABYTES);

    @BeforeEach
    public void setUp() {
        avatarService.setBucketName(bucketName);
        avatarService.setDicebearApiUrl(dicebearApiUrl);
        avatarService.setAvatarMaxSize(avatarMaxSize);
    }

    @Test
    void testGenerateRandomAvatarGenerated() throws MalformedURLException {
        String seed = "test-seed";
        String filename = "avatar.svg";
        String avatarSvg = "<svg>...</svg>";
        when(restTemplate.getForObject(any(String.class), eq(String.class))).thenReturn(avatarSvg);
        when(s3Client.doesBucketExistV2(bucketName)).thenReturn(true);
        when(s3Client.getUrl(bucketName, filename)).thenReturn(new java.net.URL("http://localhost/"
                + bucketName + "/" + filename));
        String result = avatarService.generateRandomAvatar(seed, filename);
        assertEquals("http://localhost/" + bucketName + "/" + filename, result);
        verify(s3Client).putObject(any(PutObjectRequest.class));
    }

    @Test
    void testGenerateRandomAvatarWithAvatarNotGenerated() {
        String seed = "test-seed";
        String filename = "avatar.svg";
        when(restTemplate.getForObject(any(String.class), eq(String.class))).thenReturn(null);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                avatarService.generateRandomAvatar(seed, filename));
        assertEquals("Could not generate an avatar", exception.getMessage());
    }

    @Test
    void testSaveRandomGeneratedAvatarSaved() throws Exception {
        String svg = "<svg>...</svg>";
        String filename = "avatar.svg";
        when(s3Client.doesBucketExistV2(bucketName)).thenReturn(true);
        when(s3Client.getUrl(bucketName, filename))
                .thenReturn(new java.net.URL("http://localhost/" + bucketName + "/" + filename));
        String result = avatarService.saveRandomGeneratedAvatar(svg, filename);
        assertEquals("http://localhost/" + bucketName + "/" + filename, result);
        verify(s3Client).putObject(argThat(request ->
                request.getKey().equals(filename) &&
                        request.getBucketName().equals(bucketName) &&
                        request.getCannedAcl().equals(CannedAccessControlList.PublicRead)));
    }

    @Test
    void testSaveAvatarWithFailedToSaveRandomGenerated() {
        String svg = "<svg>...</svg>";
        String filename = "avatar.svg";
        doThrow(new RuntimeException("S3 error")).when(s3Client).putObject(any(PutObjectRequest.class));
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                avatarService.saveRandomGeneratedAvatar(svg, filename));
        assertEquals("Failed to save an avatar to minio", exception.getMessage());
    }

    @Test
    void testResizeImage() throws IOException {
        int width = 200;
        int height = 100;
        int maxDimension = 50;
        BufferedImage originalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        InputStream resizedImageInputStream = avatarService.resizeImage(originalImage, maxDimension, "png");

        BufferedImage resizedImageBufferedStream = ImageIO.read(resizedImageInputStream);
        assertTrue(resizedImageBufferedStream.getWidth() <= maxDimension);
        assertTrue(resizedImageBufferedStream.getHeight() <= maxDimension);
    }

    @Test
    void testDeleteFromMinio() {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId("file id");
        userProfilePic.setSmallFileId("small file id");

        avatarService.deleteFromMinio(userProfilePic);

        verify(s3Client).deleteObject(bucketName, userProfilePic.getFileId());
        verify(s3Client).deleteObject(bucketName, userProfilePic.getSmallFileId());
    }

    @Test
    void testGetAvatar() throws MalformedURLException {
        String fileName = "file name";
        when(s3Client.getUrl(bucketName, fileName)).thenReturn(new URL("http://test"));

        avatarService.getAvatar(fileName);
        verify(s3Client).getUrl(bucketName, fileName);
    }

    @Test
    void testCheckUserHasAvatar() {
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> avatarService.checkUserHasAvatar(new User()));
        assertEquals("User doesn't have an avatar.", thrown.getMessage());
    }

    @Test
    void testConvertFromMimeType() {
        String result = avatarService.convertFromMimeType("image/png");
        assertEquals("png", result);
    }

    @Test
    void testConvertFromNonMimeType() {
        String contentType = "not supported";

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> avatarService.convertFromMimeType(contentType));
        assertEquals("Unsupported content type: " + contentType, thrown.getMessage());
    }

    @Test
    void testValidateCustomAvatarSize() {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getSize()).thenReturn(avatarMaxSize.toBytes() + 1);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> avatarService.validateCustomAvatarSize(multipartFile));
        assertEquals(String.format("The image size should not exceed %s mb", avatarMaxSize.toMegabytes()),
                thrown.getMessage());
    }

    @Test
    void testGenerateFileName() {
        String result = avatarService.generateFileName("png");
        assertTrue(result.endsWith(".png"));
    }
}
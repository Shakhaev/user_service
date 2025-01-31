package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.FileException;
import school.faang.user_service.utils.image.ImageProcessor;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {
    @Mock
    private AmazonS3 s3Client;

    @Mock
    private ImageProcessor imageProcessor;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private S3Service s3Service;

    private String testBucket = "test-bucket";
    private String avatarFolder = "avatars";
    private int largeSize = 1024;
    private int smallSize = 256;

    @Test
    void uploadAvatarSuccess() throws Exception {
        setField(s3Service, "largeAvatarMaxSize", largeSize);
        setField(s3Service, "smallAvatarMaxSize", smallSize);
        setField(s3Service, "bucketName", testBucket);
        setField(s3Service, "avatarFolderName", avatarFolder);

        ImageProcessor.ImageData largeImageData = mockImageData();
        ImageProcessor.ImageData smallImageData = mockImageData();

        when(imageProcessor.resizeImage(any(), eq(largeSize))).thenReturn(largeImageData);
        when(imageProcessor.resizeImage(any(), eq(smallSize))).thenReturn(smallImageData);

        Pair<UserProfilePic, InputStream> result = s3Service.uploadAvatar(multipartFile, "large");

        ArgumentCaptor<PutObjectRequest> putRequestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client, times(2)).putObject(putRequestCaptor.capture());

        List<PutObjectRequest> requests = putRequestCaptor.getAllValues();
        assertEquals(testBucket, requests.get(0).getBucketName());
        assertEquals(testBucket, requests.get(1).getBucketName());
        assertTrue(requests.get(0).getKey().startsWith(avatarFolder + "/"));

        assertNotNull(result.getFirst().getFileId());
        assertNotNull(result.getFirst().getSmallFileId());
        assertSame(largeImageData.getInputStream(), result.getSecond());
    }

    @Test
    void downloadAvatarNotFound() {
        setField(s3Service, "bucketName", testBucket);

        when(s3Client.getObject(testBucket, "invalid-key"))
                .thenThrow(new AmazonS3Exception("Object not found"));

        assertThrows(FileException.class, () -> {
            s3Service.downloadAvatar("invalid-key");
        });
    }

    @Test
    void deleteAvatarSuccess() {
        setField(s3Service, "bucketName", testBucket);
        String testKey = "avatars/test-image.jpg";

        s3Service.deleteAvatar(testKey);

        verify(s3Client).deleteObject(testBucket, testKey);
    }

    private ImageProcessor.ImageData mockImageData() {
        ImageProcessor.ImageData imageData = mock(ImageProcessor.ImageData.class);
        when(imageData.getInputStream()).thenReturn(mock(InputStream.class));
        when(imageData.getContentLength()).thenReturn(100L);
        when(imageData.getContentType()).thenReturn("image/jpeg");
        return imageData;
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package school.faang.user_service.service.minio;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {
    @Mock
    private AmazonS3 s3Client;
    @InjectMocks
    private MinioService minioService;

    @Test
    void testUploadSuccessful() throws MalformedURLException {
        InputStream inputStream = mock(InputStream.class);
        String fileName = "fileName";
        String bucketName = "bucketName";

        String testUrl = "https://minio.test-bucket/test-file.txt";
        when(s3Client.getUrl(bucketName, fileName)).thenReturn(new URL(testUrl));

        minioService.upload(inputStream, fileName, bucketName);
        verify(s3Client).putObject(any(PutObjectRequest.class));
        verify(s3Client).createBucket(bucketName);
        verify(s3Client).setBucketPolicy(eq(bucketName), anyString());
    }

    @Test
    void testUploadThrowsException() {
        String fileName = "fileName";
        String bucketName = "bucketName";
        InputStream inputStream = mock(InputStream.class);

        when(s3Client.putObject(any(PutObjectRequest.class))).thenThrow(mock(AmazonServiceException.class));
        assertThrows(IllegalStateException.class,
                () -> minioService.upload(inputStream, fileName, bucketName));
    }

    @Test
    void testDelete() {
        String fileName = "fileName";
        String bucketName = "bucketName";

        minioService.delete(bucketName, fileName);
        verify(s3Client).deleteObject(bucketName, fileName);
    }

    @Test
    void testGetFileUrl() throws MalformedURLException {
        String fileName = "fileName";
        String bucketName = "bucketName";

        String testUrl = "https://minio.test-bucket/test-file.txt";
        when(s3Client.getUrl(bucketName, fileName)).thenReturn(new URL(testUrl));

        minioService.getFileUrl(bucketName, fileName);
        verify(s3Client).getUrl(bucketName, fileName);
    }
}
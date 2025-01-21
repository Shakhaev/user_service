package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Service s3Service;

    private String bucketName = "test-bucket";
    private String fileKey = "test-file.svg";
    private InputStream fileContent;
    private long contentLength = 1000L;
    private String contentType = "image/svg+xml";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fileContent = new ByteArrayInputStream("file content".getBytes());
    }

    @Test
    void shouldUploadFileSuccessfully() {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        s3Service.uploadFile(fileKey, fileContent, contentLength, contentType);

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void shouldDownloadFileSuccessfully() {
        GetObjectResponse mockResponse = GetObjectResponse.builder().build();
        ResponseInputStream<GetObjectResponse> mockStream = new ResponseInputStream<>(mockResponse, fileContent);

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockStream);

        Optional<InputStream> result = s3Service.downloadFile(fileKey);

        assertTrue(result.isPresent());
        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class));
    }

    @Test
    void shouldReturnEmptyWhenFileNotFound() {
        when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(NoSuchKeyException.class);

        Optional<InputStream> result = s3Service.downloadFile(fileKey);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldDeleteFileSuccessfully() {
        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(DeleteObjectResponse.builder().build());

        s3Service.deleteFile(fileKey);

        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenUploadFails() {
        doThrow(new RuntimeException("Upload failed")).when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        assertThrows(RuntimeException.class, () -> s3Service.uploadFile(fileKey, fileContent, contentLength, contentType));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenDownloadFails() {
        doThrow(new RuntimeException("Download failed")).when(s3Client).getObject(any(GetObjectRequest.class));

        assertThrows(RuntimeException.class, () -> s3Service.downloadFile(fileKey));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenDeleteFails() {
        doThrow(new RuntimeException("Delete failed")).when(s3Client).deleteObject(any(DeleteObjectRequest.class));

        assertThrows(RuntimeException.class, () -> s3Service.deleteFile(fileKey));
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}

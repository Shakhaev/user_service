package school.faang.user_service.service.storage;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.StorageException;

import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3StorageService implements StorageService {

    private final AmazonS3 amazonS3;

    @Value("${storage.type}")
    private String storageType;

    @Value("${storage.bucketName}")
    private String bucketName;


    @Override
    public void uploadFile(String fileName, byte[] content, String contentType) {
        log.info("Loading file '{}' to the bucket '{}'.", fileName, bucketName);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(content.length);

        ByteArrayInputStream byteArray = new ByteArrayInputStream(content);
        PutObjectRequest putRequest = new PutObjectRequest(bucketName, fileName, byteArray, metadata);
        try {
            amazonS3.putObject(putRequest);
            log.info("File '{}' successfully uploaded to the bucket '{}'.", fileName, bucketName);
        } catch (AmazonServiceException exception) {
            log.error("AmazonServiceException while uploading file '{}' to the bucket '{}': {}",
                    fileName, bucketName, exception.getMessage(), exception);
            throw new StorageException("Failed to upload file: " + fileName, exception);
        } catch (SdkClientException exception) {
            log.error("SdkClientException while uploading file '{}' to the bucket '{}': {}",
                    fileName, bucketName, exception.getMessage(), exception);
            throw new StorageException("Failed to upload file: " + fileName, exception);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        log.info("Deleting file '{}' from the bucket '{}'.", fileName, bucketName);
        try {
            amazonS3.deleteObject(bucketName, fileName);
            log.info("File '{}' successfully deleted from the bucket '{}'.", fileName, bucketName);
        } catch (AmazonServiceException exception) {
            log.error("AmazonServiceException while deleting file '{}' from the bucket '{}': {}",
                    fileName, bucketName, exception.getMessage(), exception);
            throw new StorageException("Failed to delete file: " + fileName, exception);
        } catch (SdkClientException exception) {
            log.error("SdkClientException while deleting file '{}' from the bucket '{}': {}",
                    fileName, bucketName, exception.getMessage(), exception);
            throw new StorageException("Failed to delete file: " + fileName, exception);
        }
    }

    @Override
    public boolean ifFileExists(String fileName) {
        log.info("Checking file existence '{}' in bucket '{}'.", fileName, bucketName);
        try {
            boolean exists = amazonS3.doesObjectExist(bucketName, fileName);
            log.info("File '{}' exists: {}", fileName, exists);
            return exists;
        } catch (AmazonServiceException exception) {
            log.error("AmazonServiceException while checking existence of file '{}' in bucket '{}': {}",
                    fileName, bucketName, exception.getMessage(), exception);
            throw new StorageException("Failed to check if file exists: " + fileName, exception);
        } catch (SdkClientException exception) {
            log.error("SdkClientException while checking existence of file '{}' in bucket '{}': {}",
                    fileName, bucketName, exception.getMessage(), exception);
            throw new StorageException("Failed to check if file exists: " + fileName, exception);
        }
    }
}

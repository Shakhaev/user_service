package school.faang.user_service.service.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.StorageException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

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
        log.info("Начало загрузки файла '{}' в бакет '{}'.", fileName, bucketName);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(content.length);

        try (ByteArrayInputStream byteArray = new ByteArrayInputStream(content)) {
            PutObjectRequest putRequest = new PutObjectRequest(bucketName, fileName, byteArray, metadata);
            amazonS3.putObject(putRequest);
            log.info("Файл '{}' успешно загружен в бакет '{}'.", fileName, bucketName);
        } catch (IOException error) {
            log.error("IOException при загрузке файла '{}' в бакет '{}': {}",
                    fileName,
                    bucketName,
                    error.getMessage(),
                    error);
            throw new StorageException("Failed to upload file: " + fileName, error);
        } catch (RuntimeException e) {
            log.error("RuntimeException при загрузке файла '{}' в бакет '{}': {}", fileName, bucketName, e.getMessage(), e);
            throw new StorageException("Failed to upload file: " + fileName, e);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        log.info("Начало удаления файла '{}' из бакета '{}'.", fileName, bucketName);
        try {
            amazonS3.deleteObject(bucketName, fileName);
            log.info("Файл '{}' успешно удалён из бакета '{}'.", fileName, bucketName);
        } catch (RuntimeException error) {
            log.error("RuntimeException при удалении файла '{}' из бакета '{}': {}",
                    fileName,
                    bucketName,
                    error.getMessage(),
                    error);
            throw new StorageException("Failed to delete file: " + fileName, error);
        }
    }

    @Override
    public boolean ifFileExists(String fileName) {
        log.info("Проверка существования файла '{}' в бакете '{}'.", fileName, bucketName);
        try {
            boolean exists = amazonS3.doesObjectExist(bucketName, fileName);
            log.info("Файл '{}' существует: {}", fileName, exists);
            return exists;
        } catch (RuntimeException error) {
            log.error("RuntimeException при проверке существования файла '{}' в бакете '{}': {}",
                    fileName,
                    bucketName,
                    error.getMessage(),
                    error);
            throw new StorageException("Failed to check if file exists: " + fileName, error);
        }
    }
}

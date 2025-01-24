package school.faang.user_service.service.external;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.S3Exception;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class S3Service {
    private final AmazonS3 amazonS3Client;

    public String uploadToBucket(String bucketName, String fileName, byte[] data, String contentType) {
        ensureBucketExists(bucketName);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(data.length);

        executeWithS3Exception(() -> amazonS3Client.putObject(bucketName, fileName, createInputStream(data), metadata));
        return fileName;
    }

    public String getUnexpiredUrl(String bucketName, String fileId) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, fileId);
        URL url = amazonS3Client.generatePresignedUrl(request);
        return url.toString();
    }

    public String getPresignedUrlForDownload(String bucketName, String fileId, Date expirationDate) {
        URL presignedUrl = amazonS3Client.generatePresignedUrl(bucketName, fileId, expirationDate);
        return presignedUrl.toString();
    }

    private void ensureBucketExists(String bucketName) {
        executeWithS3Exception(() -> {
            if (!amazonS3Client.doesBucketExistV2(bucketName)) {
                amazonS3Client.createBucket(bucketName);
            }
        });
    }

    private void executeWithS3Exception(Runnable runnable) {
        try {
            runnable.run();
        } catch (SdkClientException e) {
            throw new S3Exception("Error working with S3");
        }
    }

    private ByteArrayInputStream createInputStream(byte[] data) {
        return new ByteArrayInputStream(data);
    }
}

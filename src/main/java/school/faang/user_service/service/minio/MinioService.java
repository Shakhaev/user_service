package school.faang.user_service.service.minio;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.actions.S3Actions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioService {
    private final AmazonS3 s3Client;

    public String upload(InputStream inputStream, String fileName, String bucketName) {
        try {
            log.debug("Start loading the file in minio.");
            createBucketIfNotExists(bucketName);

            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, fileName, inputStream, null)
                            .withCannedAcl(CannedAccessControlList.PublicRead);

            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error("Error when saving a file in minio. File name: {}.", fileName, e);
            throw new IllegalStateException(e);
        }

        log.debug("Successful completion of file upload in minio.");
        return s3Client.getUrl(bucketName, fileName).toString();
    }

    public void delete(String bucketName, String fileName) {
        s3Client.deleteObject(bucketName, fileName);
    }

    public String getFileUrl(String bucketName, String fileName) {
        return s3Client.getUrl(bucketName, fileName).toString();
    }

    private void createBucketIfNotExists(String bucketName) {
        if (!s3Client.doesBucketExistV2(bucketName)) {
            s3Client.createBucket(bucketName);
            String policyText = getPublicReadPolicy(bucketName);
            s3Client.setBucketPolicy(bucketName, policyText);
        }
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

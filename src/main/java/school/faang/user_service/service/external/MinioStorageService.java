package school.faang.user_service.service.external;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MinioStorageService {
    private static final String USER_AVATARS_BUCKET_NAME = "user-avatars";
    private final MinioClient minioClient;

    public void saveFile(String file, String fileId) throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(USER_AVATARS_BUCKET_NAME).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(USER_AVATARS_BUCKET_NAME).build());
        }

        byte[] avatarBytes = file.getBytes(StandardCharsets.UTF_8);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(avatarBytes)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(USER_AVATARS_BUCKET_NAME)
                            .object(fileId + ".svg")
                            .stream(inputStream, avatarBytes.length, -1)
                            .contentType("image/svg+xml")
                            .build()
            );
        }
    }

    public String getFile(String fileId) throws Exception {
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(USER_AVATARS_BUCKET_NAME)
                        .object(fileId + ".svg")
                        .build()
        )) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            return result.toString(StandardCharsets.UTF_8);
        }
    }
}

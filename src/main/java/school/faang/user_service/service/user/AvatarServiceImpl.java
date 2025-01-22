package school.faang.user_service.service.user;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.avatar.AvatarFeignClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {
    private static final String AVATAR_BUCKET_NAME = "avatars";
    private static final String DEFAULT_AVATAR = "default-avatar.png";

    private final MinioClient minioClient;
    private final AvatarFeignClient avatarFeignClient;

    @SneakyThrows
    @Transactional
    @Override
    public ByteArrayResource getAvatarFromDiceBear(String seed, int size) {
        try {
            return avatarFeignClient.getAvatar(seed, size);
        } catch (Exception e) {
            return getDefaultAvatar();
        }
    }

    @SneakyThrows
    @Transactional
    @Override
    public String uploadAvatar(ByteArrayResource file) {
        checkBucketExists();
        return uploadToMinio(file);
    }

    private ByteArrayResource getDefaultAvatar() throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException, InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException, XmlParserException {
        InputStream object = minioClient.getObject(GetObjectArgs.builder()
                .bucket(AVATAR_BUCKET_NAME)
                .object(DEFAULT_AVATAR)
                .build());
        byte[] bytes = object.readAllBytes();
        return new ByteArrayResource(bytes);
    }

    private void checkBucketExists() throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException, InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException, XmlParserException {
        boolean isBucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(AVATAR_BUCKET_NAME)
                .build());
        if (!isBucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(AVATAR_BUCKET_NAME)
                    .build());
        }
    }

    @SneakyThrows
    private String uploadToMinio(ByteArrayResource avatar) {
        String avatarId = UUID.randomUUID().toString();
        try {
            byte[] stream = toByteArray(avatar.getInputStream());
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(AVATAR_BUCKET_NAME)
                    .object(avatarId + ".png")
                    .stream(new ByteArrayInputStream(stream), stream.length, -1)
                    .build());
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return avatarId;
    }

    private static byte[] toByteArray(InputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("InputStream is null");
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = stream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toByteArray();
        }
    }
}

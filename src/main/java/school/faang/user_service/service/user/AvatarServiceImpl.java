package school.faang.user_service.service.user;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
    private final MinioClient minioClient;
    private final AvatarFeignClient avatarFeignClient;


    @Override
    public ByteArrayResource generateAvatar(String seed, int size) {
        return avatarFeignClient.getAvatar(seed, size);
    }

    @SneakyThrows
    @Override
    public void uploadAvatar(ByteArrayResource file, String uuid) {
        boolean isBucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(AVATAR_BUCKET_NAME)
                .build());
        if (!isBucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(AVATAR_BUCKET_NAME)
                    .build());
        }
        uploadToMinio(file, uuid);
    }

    private void uploadToMinio(ByteArrayResource avatar, String avatarId) throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException, InvalidResponseException, NoSuchAlgorithmException, ServerException, XmlParserException {
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

    @SneakyThrows
    @Override
    public void uploadAvatar(MultipartFile file) {
        boolean isBucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(AVATAR_BUCKET_NAME)
                .build());
        if (!isBucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(AVATAR_BUCKET_NAME)
                    .build());
        }
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(AVATAR_BUCKET_NAME)
                .object(getFileName(file))
                .stream(file.getInputStream(), file.getSize(), -1)
                .build());
    }

    private String getFileName(MultipartFile file) {
        StringBuilder stringBuilder = new StringBuilder();
        String fileExtension = getFileExtension(file);
        return stringBuilder.append(UUID.randomUUID())
                .append(fileExtension)
                .toString();
    }

    private String getFileExtension(MultipartFile file) {
        if (file.getOriginalFilename() != null && file.getOriginalFilename().isEmpty()) {
            return file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        }
        throw new IllegalArgumentException("Filename is required");
    }
}

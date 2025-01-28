package school.faang.user_service.service.user;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.avatar.AvatarFeignClient;
import school.faang.user_service.entity.User;
import school.faang.user_service.properties.MinioProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {
    private static final int AVATAR_SIZE = 64;
    private static final int SMALL_AVATAR_SIZE = 32;
    private static final String AVATAR_BUCKET_NAME = "avatars";

    private final MinioClient minioClient;
    private final AvatarFeignClient avatarFeignClient;
    private final MinioProperties minioProperties;

    @Transactional
    public Pair<String, String> saveAvatarsToMinio(User user) {
        String randomId = UUID.randomUUID().toString();
        ByteArrayResource avatar = getAvatarFromDiceBear(randomId, AVATAR_SIZE);
        ByteArrayResource smallAvatar = getAvatarFromDiceBear(randomId, SMALL_AVATAR_SIZE);

        String avatarId = uploadAvatarToMinio(avatar);
        String avatarSmallId = uploadAvatarToMinio(smallAvatar);

        return Pair.of(avatarId, avatarSmallId);
    }

    @SneakyThrows
    private ByteArrayResource getAvatarFromDiceBear(String seed, int size) {
        try {
            return avatarFeignClient.getAvatar(seed, size);
        } catch (Exception e) {
            return getDefaultAvatar();
        }
    }

    @SneakyThrows
    private ByteArrayResource getDefaultAvatar() {
        InputStream object = minioClient.getObject(GetObjectArgs.builder()
                .bucket(AVATAR_BUCKET_NAME)
                .object(minioProperties.getDefaultAvatar())
                .build());
        byte[] bytes = object.readAllBytes();
        return new ByteArrayResource(bytes);
    }

    @SneakyThrows
    private String uploadAvatarToMinio(ByteArrayResource file) {
        checkBucketExists();
        String avatarId = UUID.randomUUID().toString();
        try {
            InputStream stream = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(AVATAR_BUCKET_NAME)
                    .object(avatarId + ".png")
                    .stream(stream, stream.available(), -1)
                    .build());
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return avatarId;
    }

    @SneakyThrows
    private void checkBucketExists() {
        boolean isBucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(AVATAR_BUCKET_NAME)
                .build());
        if (!isBucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(AVATAR_BUCKET_NAME)
                    .build());
        }
    }
}

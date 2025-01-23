package school.faang.user_service.service.user;

import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import school.faang.user_service.client.avatar.AvatarFeignClient;
import school.faang.user_service.entity.User;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvatarServiceImplTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private AvatarFeignClient feignClient;

    @InjectMocks
    private AvatarServiceImpl service;

    @Test
    public void testSaveAvatarsToMinio() throws IOException, ServerException, InsufficientDataException,
            ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {
        byte[] image = "image".getBytes(StandardCharsets.UTF_8);
        ByteArrayResource byteArrayResource = new ByteArrayResource(image);
        when(feignClient.getAvatar(eq("random"), eq(32))).thenReturn(byteArrayResource);

        commonTest();
    }

    @Test
    public void testSaveAvatarsToMinioWhenFeignClientNotAvailable() throws IOException, ServerException,
            InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        when(feignClient.getAvatar(eq("random"), eq(32))).thenThrow(RuntimeException.class);

        commonTest();
    }

    public void commonTest() throws IOException, ServerException, InsufficientDataException,
            ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        byte[] image = "image".getBytes(StandardCharsets.UTF_8);
        ByteArrayResource byteArrayResource = new ByteArrayResource(image);
        InputStream inputStream = byteArrayResource.getInputStream();
        GetObjectResponse getObjectResponse = new GetObjectResponse(
                null, "bucket", null, "default", inputStream
        );
        ObjectWriteResponse mock = mock(ObjectWriteResponse.class);
        when(minioClient.putObject(any())).thenReturn(mock);
        when(minioClient.getObject(any())).thenReturn(getObjectResponse);

        Pair<String, String> avatars = service.saveAvatarsToMinio(User.builder().build());

        assertNotNull(avatars);
    }

}
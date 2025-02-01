package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.filters.avatar.AvatarFilter;
import school.faang.user_service.service.minio.ImageService;
import school.faang.user_service.service.minio.MinioService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvatarServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private MinioService minioService;
    @Mock
    private ImageService imageService;
    @InjectMocks
    private AvatarService avatarService;

    @Value("${minio.avatarBucket}")
    private String bucketName = "test-bucket";
    @Value("${dicebear.api.url}")
    private String dicebearApiUrl = "http://localhost/dicebear";
    @Value("${minio.avatar.max-size}")
    private DataSize avatarMaxSize = DataSize.of(5, DataUnit.MEGABYTES);

    @BeforeEach
    public void setUp() {
        avatarService.setBucketName(bucketName);
        avatarService.setDicebearApiUrl(dicebearApiUrl);
        avatarService.setAvatarMaxSize(avatarMaxSize);
    }

    @Test
    void testGenerateRandomAvatarGenerated() {
        String seed = "test-seed";
        String filename = "avatar.svg";
        String avatarSvg = "<svg>...</svg>";
        when(restTemplate.getForObject(any(String.class), eq(String.class))).thenReturn(avatarSvg);

        avatarService.generateRandomAvatar(seed, filename);
        verify(minioService).upload(any(InputStream.class), eq(filename), eq(bucketName));
    }

    @Test
    void testGenerateRandomAvatarWithAvatarNotGenerated() {
        String seed = "test-seed";
        String filename = "avatar.svg";
        when(restTemplate.getForObject(any(String.class), eq(String.class))).thenReturn(null);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                avatarService.generateRandomAvatar(seed, filename));
        assertEquals("Could not generate an avatar", exception.getMessage());
    }

    @Test
    void testUploadCustomAvatarSuccessful() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        AvatarFilter filter = mock(AvatarFilter.class);

        ReflectionTestUtils.setField(avatarService, "avatarFilters", List.of(filter));
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

        UserProfilePic result = avatarService.uploadCustomAvatar(file);
        verify(filter).resizeAndUploadToMinio(any(), any(), any(UserProfilePic.class));
        assertNotNull(result);
    }

    @Test
    void testUploadCustomAvatarThrowsException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("Test IOException"));

        assertThrows(RuntimeException.class, () -> avatarService.uploadCustomAvatar(file));
    }

    @Test
    void testDeleteAvatar() {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId("file id");
        userProfilePic.setSmallFileId("small file id");

        avatarService.deleteAvatar(userProfilePic);

        verify(minioService).delete(bucketName, userProfilePic.getFileId());
        verify(minioService).delete(bucketName, userProfilePic.getSmallFileId());
    }

    @Test
    void testGetSmallAvatar() {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setSmallFileId("small file id");
        when(minioService.getFileUrl(bucketName, userProfilePic.getSmallFileId()))
                .thenReturn("small avatar url");

        avatarService.getAvatar(userProfilePic, true);
        verify(minioService).getFileUrl(bucketName, userProfilePic.getSmallFileId());
    }

    @Test
    void testGetBigAvatar() {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId("big file id");
        when(minioService.getFileUrl(bucketName, userProfilePic.getFileId()))
                .thenReturn("big avatar url");

        avatarService.getAvatar(userProfilePic, false);
        verify(minioService).getFileUrl(bucketName, userProfilePic.getFileId());
    }

    @Test
    void testCheckUserHasAvatar() {
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> avatarService.checkUserHasAvatar(new User()));
        assertEquals("User doesn't have an avatar.", thrown.getMessage());
    }

    @Test
    void testValidateCustomAvatarSize() {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getSize()).thenReturn(avatarMaxSize.toBytes() + 1);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> avatarService.validateCustomAvatarSize(multipartFile));
        assertEquals(String.format("The image size should not exceed %s mb", avatarMaxSize.toMegabytes()),
                thrown.getMessage());
    }
}
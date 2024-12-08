package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.Integrations.avatar.AvatarService;
import school.faang.user_service.util.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvatarServiceTest {

    @InjectMocks
    private AvatarService avatarService;

    @Mock
    private S3Service s3Service;

    @Mock
    private ImageUtils imageUtils;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(avatarService, "baseUrl", "https://avatars.dicebear.com");
        ReflectionTestUtils.setField(avatarService, "version", "v2");
        ReflectionTestUtils.setField(avatarService, "styles", List.of("bottts", "avataaars"));
        ReflectionTestUtils.setField(avatarService, "seedNames", List.of("user1", "user2"));
    }

    @Test
    void generateAndUploadUserAvatars_success() throws IOException {
        String userId = "123";
        BufferedImage dummyImage = mock(BufferedImage.class);
        BufferedImage resizedImageLarge = mock(BufferedImage.class);
        BufferedImage resizedImageSmall = mock(BufferedImage.class);

        when(imageUtils.resizeImage(dummyImage, 1080)).thenReturn(resizedImageLarge);
        when(imageUtils.resizeImage(dummyImage, 170)).thenReturn(resizedImageSmall);
        when(s3Service.uploadImage(any(), eq("avatars"), any(), eq(resizedImageLarge))).thenReturn("largeFileId");
        when(s3Service.uploadImage(any(), eq("avatars"), any(), eq(resizedImageSmall))).thenReturn("smallFileId");

        UserProfilePic userProfilePic = avatarService.generateAndUploadUserAvatars(userId);

        assertThat(userProfilePic).isNotNull();
        assertThat(userProfilePic.getFileId()).isEqualTo("largeFileId");
        assertThat(userProfilePic.getSmallFileId()).isEqualTo("smallFileId");

        verify(s3Service, times(2)).uploadImage(any(), eq("avatars"), any(), any());
    }

    @Test
    void generateAndUploadUserAvatars_invalidConfiguration_throwsException() {
        ReflectionTestUtils.setField(avatarService, "styles", new ArrayList<>());

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> avatarService.generateAndUploadUserAvatars(null)
        );
        assertThat(exception.getMessage()).isEqualTo("Invalid DiceBear configuration");
    }

    @Test
    void uploadAvatar_success() throws IOException {
        String userId = "123";
        BufferedImage dummyImage = mock(BufferedImage.class);
        BufferedImage resizedImage = mock(BufferedImage.class);

        when(imageUtils.resizeImage(dummyImage, 1080)).thenReturn(resizedImage);
        when(s3Service.uploadImage(any(), eq("avatars"), any(), eq(resizedImage))).thenReturn("fileId");

        String result = avatarService.uploadAvatar(dummyImage, userId, false);

        assertThat(result).isEqualTo("fileId");
        verify(imageUtils).resizeImage(dummyImage, 1080);
        verify(s3Service).uploadImage(any(), eq("avatars"), any(), eq(resizedImage));
    }

    @Test
    void uploadAvatar_resizeFails_throwsException() throws IOException {
        String userId = "123";
        BufferedImage dummyImage = mock(BufferedImage.class);

        when(imageUtils.resizeImage(dummyImage, 1080)).thenThrow(new IOException("Resize failed"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> avatarService.uploadAvatar(dummyImage, userId, false)
        );
        assertThat(exception.getMessage()).isEqualTo("Failed to process and upload avatar");
        verify(imageUtils).resizeImage(dummyImage, 1080);
    }
}

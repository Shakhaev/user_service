package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.Integrations.avatar.AvatarService;
import school.faang.user_service.util.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvatarServiceTest {

    @Spy
    @InjectMocks
    private AvatarService avatarService;

    @Mock
    private S3Service s3Service;

    @Mock
    private ImageUtils imageUtils;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(avatarService, "baseUrl", "https://api.dicebear.com");
        ReflectionTestUtils.setField(avatarService, "version", "9.x");
        ReflectionTestUtils.setField(avatarService, "styles", List.of("bottts", "adventurer"));
        ReflectionTestUtils.setField(avatarService, "seedNames", List.of("Brian", "Adrian"));
    }

    @Test
    void generateUrlRandomAvatar_success() {
        ReflectionTestUtils.setField(avatarService, "styles", List.of("bottts"));
        ReflectionTestUtils.setField(avatarService, "seedNames", List.of("Brian"));

        String url = ReflectionTestUtils.invokeMethod(avatarService, "generateUrlRandomAvatar");

        assertThat(url).isNotNull();
        assertThat(url).contains("https://api.dicebear.com/9.x");
        assertThat(url).contains("bottts");
        assertThat(url).contains("?seed=");
        assertThat(url).contains("Brian");
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
    void uploadAvatar_success(){
        String userId = "123";

        BufferedImage dummyImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        BufferedImage resizedImage = new BufferedImage(1080, 1080, BufferedImage.TYPE_INT_ARGB);

        when(imageUtils.resizeImage(dummyImage, 1080)).thenReturn(resizedImage);
        when(s3Service.uploadImage(any(), eq("avatars"), any(), eq(resizedImage))).thenReturn("fileId");

        String result = avatarService.uploadAvatar(dummyImage, userId, false);

        assertThat(result).isEqualTo("fileId");
        verify(imageUtils).resizeImage(dummyImage, 1080);
        verify(s3Service).uploadImage(any(), eq("avatars"), any(), eq(resizedImage));
    }

    @Test
    void uploadAvatar_resizeFails_throwsException() {
        String userId = "123";
        BufferedImage dummyImage = mock(BufferedImage.class);

        when(imageUtils.resizeImage(dummyImage, 1080))
                .thenThrow(new RuntimeException("Failed to process and upload avatar"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> avatarService.uploadAvatar(dummyImage, userId, false)
        );

        assertThat(exception.getMessage()).isEqualTo("Failed to process and upload avatar");
        verify(imageUtils).resizeImage(dummyImage, 1080);
    }

}

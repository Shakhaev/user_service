package school.faang.user_service.filters.avatar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.service.user.AvatarService;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BigAvatarFilterTest {
    @Mock
    private AvatarService avatarService;
    @InjectMocks
    private BigAvatarFilter bigAvatarFilter;

    @Test
    public void testResizeAndUploadToMinio() {
        String formatName = "png";
        String minioKey = "test key";
        BufferedImage originalImage = mock(BufferedImage.class);
        UserProfilePic userProfilePic = mock(UserProfilePic.class);
        InputStream resizedImage = mock(InputStream.class);

        when(avatarService.generateFileName(formatName)).thenReturn(minioKey);
        when(avatarService.resizeImage(eq(originalImage), anyInt(), eq(formatName))).thenReturn(resizedImage);


        bigAvatarFilter.resizeAndUploadToMinio(originalImage, formatName, userProfilePic);

        verify(avatarService).uploadToMinio(resizedImage, minioKey);
        verify(userProfilePic).setFileId(minioKey);
    }
}
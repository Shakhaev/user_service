package school.faang.user_service.service.avatar;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfilePicServiceImplTest {
    @InjectMocks
    private ProfilePicServiceImpl profilePicService;
    @Mock
    private AmazonS3 s3Client;
    @Mock
    private RestTemplate restTemplate;
    private User user;

    private byte[] getImageBytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.RED);
        graphics.fill(new Rectangle2D.Double(0, 0, 50, 50));
        byte[] bytes;
        try {
            ImageIO.write(image, "jpg", outputStream);
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }

    @BeforeEach
    public void setUp(){
        user = User.builder().username("name").email("test@mail.ru").password("password").build();
    }

    @Test
    public void testGenerateAndSetPicWithException(){
        when(restTemplate.getForObject(anyString(),eq(byte[].class))).thenReturn(null);
        var exception = assertThrows(DataValidationException.class, ()->profilePicService.generateAndSetPic(user));
        assertEquals(exception.getMessage(), "Failed to get the generated image");
    }

    @Test
    public void testGenerateAndSetPicWithSetting(){
        when(restTemplate.getForObject(any(String.class),eq(byte[].class))).thenReturn(getImageBytes());
        when(s3Client.putObject(anyString(), anyString(), any(InputStream.class), any(ObjectMetadata.class))).thenReturn(null);
        ReflectionTestUtils.setField(profilePicService, "smallSize", 170);
        ReflectionTestUtils.setField(profilePicService, "bucketName", "user-bucket");

        profilePicService.generateAndSetPic(user);
        UserProfilePic generated = user.getUserProfilePic();
        assertNull(generated.getFileId());
        assertNotNull(generated.getSmallFileId());

        InOrder inorder = inOrder(restTemplate, s3Client);
        inorder.verify(restTemplate, times(1)).getForObject(anyString(),eq(byte[].class));
        inorder.verify(s3Client, times(1)).putObject(anyString(), anyString(), any(InputStream.class), any(ObjectMetadata.class));
    }
}

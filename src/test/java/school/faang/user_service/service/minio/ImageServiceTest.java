package school.faang.user_service.service.minio;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {
    private final ImageService imageService = new ImageService();

    @Test
    void testResizeAvatar() throws IOException {
        int width = 200;
        int height = 100;
        int maxDimension = 50;
        BufferedImage originalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        InputStream resizedImageInputStream = imageService.resizeImage(originalImage, maxDimension, "png");

        BufferedImage resizedImageBufferedStream = ImageIO.read(resizedImageInputStream);
        assertTrue(resizedImageBufferedStream.getWidth() <= maxDimension);
        assertTrue(resizedImageBufferedStream.getHeight() <= maxDimension);
    }

    @Test
    void testConvertFromMimeType() {
        String result = imageService.convertFromMimeType("image/png");
        assertEquals("png", result);
    }

    @Test
    void testConvertFromNonMimeType() {
        String contentType = "not supported";

        assertThrows(IllegalArgumentException.class, () -> imageService.convertFromMimeType(contentType));
    }

    @Test
    void testGenerateAvatarFileName() {
        String result = imageService.generateImageName("png");
        assertTrue(result.endsWith(".png"));
    }
}
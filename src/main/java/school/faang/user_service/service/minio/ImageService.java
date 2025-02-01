package school.faang.user_service.service.minio;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageService {
    public InputStream resizeImage(BufferedImage originalImage, int maxDimension, String formatName) {
        try {
            BufferedImage resizedImage = Scalr.resize(originalImage,
                    Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, maxDimension);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, formatName, outputStream);

            log.debug("""
                    Successfully change the resolution of the image.
                    Max dimension: {}; Format name: {}.
                    """, maxDimension, formatName);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            log.error("""
                    Error while changing the image resolution.
                    Max dimension: {}; Format name: {}.
                    """, maxDimension, formatName, e);
            throw new RuntimeException(e);
        }
    }

    public String convertFromMimeType(String mimeType) {
        if (mimeType != null && mimeType.startsWith("image/")) {
            return mimeType.substring("image/".length());
        } else {
            log.error("Unsupported minio type: {}.", mimeType);
            throw new IllegalArgumentException();
        }
    }

    public String generateImageName(String formatName) {
        return UUID.randomUUID() + "." + formatName;
    }
}

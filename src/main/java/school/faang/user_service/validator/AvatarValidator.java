package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.exception.AccessDeniedException;
import school.faang.user_service.exception.FileSizeExceededException;
import school.faang.user_service.exception.InvalidFileFormatException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Component
public class AvatarValidator {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> SUPPORTED_CONTENT_TYPES = Set.of("image/jpeg", "image/jpg", "image/png", "image/gif");

    public void validateAvatarFile(MultipartFile avatarFile) {
        validateFileSize(avatarFile);
        validateContentType(avatarFile);
        validateImageContent(avatarFile);
    }

    public void isAuthorized(Long currentUserId, Long userId) {
        if (!currentUserId.equals(userId)) {
            throw new AccessDeniedException("You are not authorized to update avatar");
        }
    }

    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeExceededException("File size should not exceed " + MAX_FILE_SIZE / (1024 * 1024) + " MB");
        }
    }

    private void validateContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !isSupportedContentType(contentType)) {
            throw new InvalidFileFormatException("Uploaded file has invalid type");
        }
    }

    private boolean isSupportedContentType(String contentType) {
        return SUPPORTED_CONTENT_TYPES.contains(contentType);
    }

    private void validateImageContent(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                throw new InvalidFileFormatException("Uploaded file is not a valid image");
            }
        } catch (IOException error) {
            throw new InvalidFileFormatException("Uploaded file is not a valid image", error);
        }
    }
}

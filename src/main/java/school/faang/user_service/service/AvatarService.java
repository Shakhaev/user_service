package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.AvatarNotFoundException;
import school.faang.user_service.exception.AvatarProcessingException;
import school.faang.user_service.exception.InvalidFileFormatException;
import school.faang.user_service.service.storage.StorageService;
import school.faang.user_service.validator.AvatarValidator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {
    private static final int LARGE_IMAGE_MAX_SIZE = 1080;
    private static final int SMALL_IMAGE_MAX_SIZE = 170;

    private final StorageService storageService;
    private final UserService userService;
    private final AvatarValidator avatarValidator;

    @Transactional
    public void uploadUserAvatar(Long userId, Long currentUserId, MultipartFile userAvatarPicture) {
        log.info("User with ID {} is attempting to upload an avatar.", currentUserId);
        avatarValidator.isAuthorized(currentUserId, userId);
        avatarValidator.validateAvatarFile(userAvatarPicture);

        User user = userService.findUserById(userId);

        deleteExistingAvatarIfPresent(user);

        UserProfilePic userProfilePic = processAvatar(userId, userAvatarPicture);
        uploadAvatarFiles(userProfilePic, userAvatarPicture, getImageFormatFromContentType(
                Objects.requireNonNull(userAvatarPicture.getContentType()))
        );

        user.setUserProfilePic(userProfilePic);
        userService.saveUser(user);
        log.info("Avatar uploaded successfully for user ID {}.", userId);
    }

    @Transactional
    public void deleteUserAvatar(Long userId, Long currentUserId) {
        log.info("User with ID {} is attempting to delete avatar.", currentUserId);
        avatarValidator.isAuthorized(currentUserId, userId);
        User user = userService.findUserById(userId);

        if (user.getUserProfilePic() == null) {
            log.warn("User with ID {} does not have an avatar to delete.", userId);
            throw new AvatarNotFoundException("User with ID " + userId + " does not have an avatar to delete");
        }

        deleteExistingAvatarIfPresent(user);
        user.setUserProfilePic(null);
        userService.saveUser(user);
        log.info("Avatar deleted successfully for user ID {}.", userId);
    }

    private byte[] createThumbnail(MultipartFile userAvatarPicture, int size, String imageFormat) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Thumbnails.of(userAvatarPicture.getInputStream())
                    .size(size, size)
                    .outputFormat(imageFormat)
                    .toOutputStream(outputStream);
            log.info("Thumbnail created with size {}x{} and format {}.", size, size, imageFormat);
            return outputStream.toByteArray();
        } catch (IOException error) {
            log.error("Error processing avatar image for user.", error);
            throw new AvatarProcessingException("Error processing avatar image.", error);
        }
    }

    private void deleteAvatarFiles(UserProfilePic avatar) {
        storageService.deleteFile(avatar.getFileId());
        storageService.deleteFile(avatar.getSmallFileId());
    }

    private UserProfilePic createUserProfilePic(String largeImageFileName, String smallImageFileName) {
        return UserProfilePic.builder()
                .fileId(largeImageFileName)
                .smallFileId(smallImageFileName)
                .build();
    }

    private String getImageFormatFromContentType(String contentType) {
        return switch (contentType) {
            case "image/jpeg", "image/jpg" -> "jpeg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            default -> throw new InvalidFileFormatException("Unsupported image format");
        };
    }

    private void deleteExistingAvatarIfPresent(User user) {
        UserProfilePic existingAvatar = user.getUserProfilePic();
        if (existingAvatar != null) {
            log.info("Existing avatar found for user ID {}. Proceeding to delete old avatars.", user.getId());
            deleteAvatarFiles(existingAvatar);
            user.setUserProfilePic(null);
            log.info("Old avatars deleted successfully for user ID {}.", user.getId());
        }
    }

    private UserProfilePic processAvatar(Long userId, MultipartFile userAvatarPicture) {
        String contentType = userAvatarPicture.getContentType();
        String imageFormat = getImageFormatFromContentType(contentType);

        String largeImageFileName = "avatars/avatar_userId_" + userId + "_large." + imageFormat;
        String smallImageFileName = "avatars/avatar_userId_" + userId + "_small." + imageFormat;

        UserProfilePic userProfilePic = createUserProfilePic(largeImageFileName, smallImageFileName);

        log.info("Processed avatar for user ID {}: largeImageFileName={}, smallImageFileName={}",
                userId,
                largeImageFileName,
                smallImageFileName);
        return userProfilePic;
    }

    private void uploadAvatarFiles(UserProfilePic userProfilePic, MultipartFile userAvatarPicture, String imageFormat) {
        try {
            byte[] largeImageBytes = createThumbnail(userAvatarPicture, LARGE_IMAGE_MAX_SIZE, imageFormat);
            byte[] smallImageBytes = createThumbnail(userAvatarPicture, SMALL_IMAGE_MAX_SIZE, imageFormat);

            storageService.uploadFile(userProfilePic.getFileId(), largeImageBytes, "image/" + imageFormat);
            storageService.uploadFile(userProfilePic.getSmallFileId(), smallImageBytes, "image/" + imageFormat);
            log.info("Avatar files uploaded successfully: {} and {}", userProfilePic.getFileId(), userProfilePic.getSmallFileId());
        } catch (Exception error) {
            log.error("Error uploading avatar files for user.", error);
            throw new AvatarProcessingException("Error processing avatar image.", error);
        }
    }
}

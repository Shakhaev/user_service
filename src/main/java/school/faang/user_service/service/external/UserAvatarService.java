package school.faang.user_service.service.external;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.AppConfig;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.FileSizeIncorrectException;
import school.faang.user_service.exception.UserProfileWasNotFound;
import school.faang.user_service.exception.UserWasNotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.io.*;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAvatarService {
    private final UserRepository userRepository;
    private final MinioClient minioClient;
    private final AppConfig appConfig;

    @Transactional
    public UserProfilePic uploadAvatar(Long userId, MultipartFile multipartFile) {
        checkFileSize(multipartFile);
        User user = findUserById(userId);
        int largeFileSize = appConfig.getLargeFileSize();
        int smallFileSize = appConfig.getSmallFileSize();

        ByteArrayOutputStream largeStream = new ByteArrayOutputStream();
        ByteArrayOutputStream smallStream = new ByteArrayOutputStream();

        try {
            resizeImage(multipartFile, largeFileSize, largeStream);
            resizeImage(multipartFile, smallFileSize, smallStream);

            String largeFileId = uploadToMinio(largeStream);
            String smallFileId = uploadToMinio(smallStream);

            UserProfilePic userProfilePic = new UserProfilePic();
            userProfilePic.setFileId(largeFileId);
            userProfilePic.setSmallFileId(smallFileId);

            user.setUserProfilePic(userProfilePic);

            return userProfilePic;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new UserProfilePic();
    }

    @Transactional(readOnly = true)
    public byte[] getProfilePicture(Long userId) {
        User user = findUserById(userId);

        UserProfilePic userProfilePic = user.getUserProfilePic();
        if (userProfilePic == null || userProfilePic.getFileId() == null) {
            throw new UserProfileWasNotFound("Profile picture not found -> ID : " + userId);
        }

        try {
            return getImageFromMinio(userProfilePic.getFileId());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return new byte[0];
    }

    @Transactional
    public void deleteProfilePic(Long userId) {
        String bucketName = appConfig.getBucketName();
        User user = findUserById(userId);
        String largeFileId = user.getUserProfilePic().getFileId();
        String smallFileId = user.getUserProfilePic().getSmallFileId();

        try {
            RemoveObjectArgs largeFileArgs = RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(largeFileId)
                    .build();
            minioClient.removeObject(largeFileArgs);

            RemoveObjectArgs smallFileArgs = RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(smallFileId)
                    .build();
            minioClient.removeObject(smallFileArgs);

            user.getUserProfilePic().setFileId(null);
            user.getUserProfilePic().setSmallFileId(null);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private byte[] getImageFromMinio(String fileId) {
        String bucketName = appConfig.getBucketName();
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(fileId)
                .build();

        try (InputStream inputStream = minioClient.getObject(getObjectArgs)) {
            return inputStream.readAllBytes();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return new byte[0];
    }

    @Transactional(readOnly = true)
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserWasNotFoundException("User was not found -> ID : " + userId));
    }

    private void resizeImage(MultipartFile multipartFile,
                             int maxSize,
                             OutputStream stream) throws IOException {
        Thumbnails.of(multipartFile.getInputStream())
                .size(maxSize, maxSize)
                .toOutputStream(stream);
    }

    private String uploadToMinio(ByteArrayOutputStream stream) {
        String filename = "profile-" + LocalDateTime.now() + ".jpg";
        String bucketName = appConfig.getBucketName();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(stream.toByteArray());

        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(filename)
                .stream(byteArrayInputStream, stream.size(), -1)
                .contentType("image/jpeg")
                .build();

        try {
            minioClient.putObject(putObjectArgs);
            return filename;
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return "";
    }

    private void checkFileSize(MultipartFile multipartFile) {
        int fileSize = appConfig.getFileSize();
        if (multipartFile.getSize() > fileSize) {
            throw new FileSizeIncorrectException("File size should be less than -> " + fileSize);
        }
    }
}

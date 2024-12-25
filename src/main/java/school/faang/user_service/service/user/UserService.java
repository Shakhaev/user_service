package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.dto.UserRegistrationDto;
import school.faang.user_service.dto.UserSubResponseDto;
import school.faang.user_service.dto.user.UserForNotificationDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.filter.userFilter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserProfilePicMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.CountryService;
import school.faang.user_service.service.Integrations.avatar.AvatarService;
import school.faang.user_service.service.PasswordService;
import school.faang.user_service.service.S3Service;
import school.faang.user_service.util.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private static final int MAX_AVATAR_SIZE_MEGABYTE = 5;
    private static final int LARGE_PHOTO_SIDE_SIZE = 1080;
    private static final int SMALL_PHOTO_SIDE_SIZE = 170;
    private static final String FOLDER_NAME = "user_profile_avatar";

    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    private final List<UserFilter> filters;
    private final UserMapper userMapper;
    private final S3Service s3Service;
    private final UserProfilePicMapper userProfilePicMapper;
    private final ImageUtils imageUtils;
    private final AvatarService avatarService;
    private final CountryService countryService;
    private final PasswordService passwordService;

    @Transactional
    public void banUsers(List<Long> userIdsToBan) {
        log.info("Trying to ban users: {}", userIdsToBan);
        List<User> usersToBan = getAllUsersByIds(userIdsToBan);
        usersToBan.forEach(User::ban);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id:%d не найден".formatted(id)));
    }

    public List<User> getAllUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void updateAllUsers(List<User> users) {
        userRepository.saveAll(users);
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new
                EntityNotFoundException("User do not found by " + userId));
    }

    public UserForNotificationDto getUserByIdForNotification(long userId) {
        User user = getUserById(userId);
        return userMapper.toUserForNotificationDto(user);
    }

    public UserSubResponseDto registerUser(UserRegistrationDto userRegistrationDto) {
        if (userRepository.existsByEmail(userRegistrationDto.email())) {
            throw new DataValidationException(String.format("Пользователь с почтой %s уже зарегистрирован.", userRegistrationDto.email()));
        }

        Country country = countryService.getCountryById(userRegistrationDto.countryId());

        User user = userMapper.toEntity(userRegistrationDto);
        user.setCountry(country);
        user.setPassword(passwordService.encodePassword(userRegistrationDto.password()));

        userRepository.save(user);

        try {
            UserProfilePic profilePic = avatarService.generateAndUploadUserAvatars(user.getId().toString());

            user.setUserProfilePic(profilePic);

            userRepository.save(user);
        } catch (Exception e) {
            log.error("Error generating avatar for user {}", user.getId(), e);
        }

        return new UserSubResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getUserProfilePic());
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public List<UserSubResponseDto> getPremiumUsers(UserFilterDto userFilterDto) {
        return userMapper.toUserSubResponseList(
                filterUsers(premiumRepository.findPremiumUsers(), userFilterDto));
    }

    private List<User> filterUsers(Stream<User> users, UserFilterDto filterDto) {
        return users.filter(user -> filters.stream()
                        .filter(filter -> filter.isApplicable(filterDto))
                        .allMatch(filter -> filter.apply(user)))
                .toList();
    }

    public UserSubResponseDto getUserDtoById(long userId) {
        User user = getUserById(userId);
        return userMapper.toUserSubResponseDto(user);
    }

    public List<UserSubResponseDto> getAllUsersDtoByIds(List<Long> ids) {
        List<User> users = getAllUsersByIds(ids);
        return userMapper.toUserSubResponseList(users);
    }

    public void banUser(Long userId) {
        User user = getUserById(userId);
        user.setBanned(true);
        userRepository.save(user);
    }

    public UserProfilePicDto updateUserProfilePicture(Long userId, MultipartFile file) {
        log.info("Request to update profile picture for user {}", userId);
        User user = getUserById(userId);
        validateAvatarSize(file);

        BufferedImage originalImage = imageUtils.convertMultiPartFileToBufferedImage(file);
        BufferedImage largeImage = imageUtils.resizeImage(originalImage, LARGE_PHOTO_SIDE_SIZE);
        BufferedImage smallImage = imageUtils.resizeImage(originalImage, SMALL_PHOTO_SIDE_SIZE);

        String fileId = s3Service.uploadImage(file, FOLDER_NAME,
                "avatarPictureUser%s".formatted(user.getId()), largeImage);
        String smallFileId = s3Service.uploadImage(file, FOLDER_NAME,
                "smallAvatarPictureUser%s".formatted(user.getId()), smallImage);

        UserProfilePic profilePic = UserProfilePic.builder()
                .fileId(fileId)
                .smallFileId(smallFileId)
                .build();
        user.setUserProfilePic(profilePic);
        updateUser(user);

        return userProfilePicMapper.userProfilePicToDto(profilePic);
    }

    public InputStreamResource getUserAvatar(long userId) {
        User user = getUserById(userId);
        log.info("Request to get profile picture for user {}", user.toStringProfilePicInfo());
        if (user.getUserProfilePic() == null) {
            return new InputStreamResource(new ByteArrayInputStream(new byte[0]));
        }
        String key = user.getUserProfilePic().getFileId();
        return s3Service.getFile(key);
    }

    public void deleteUserAvatar(long userId) {
        User user = getUserById(userId);
        log.info("Request to delete profile picture for user {}", user.toStringProfilePicInfo());
        if (user.getUserProfilePic() == null) {
            return;
        }
        String fileId = user.getUserProfilePic().getFileId();
        String smallFileId = user.getUserProfilePic().getSmallFileId();
        user.deleteUserProfilePic();
        updateUser(user);

        s3Service.deleteFiles(fileId, smallFileId);
    }

    private void validateAvatarSize(MultipartFile file) {
        String contentType = file.getContentType();
        double fileSize = bytesToMegabytes(file.getSize());

        if (!contentType.contains("image")) {
            throw new DataValidationException("The file is not image");
        }
        if (fileSize > MAX_AVATAR_SIZE_MEGABYTE) {
            throw new DataValidationException("The file size exceeds 5 megabytes");
        }
    }

    private double bytesToMegabytes(long bytes) {
        return bytes / (Math.pow(1024, 2));
    }
}

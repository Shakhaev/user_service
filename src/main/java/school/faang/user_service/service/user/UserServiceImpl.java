package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final int AVATAR_SIZE = 64;
    private static final int SMALL_AVATAR_SIZE = 32;

    private final UserRepository userRepository;
    private final AvatarService avatarService;

    @Override
    public void saveRandomAvatar(long userId) {
        //get generated avatars: big and small (with one uuid)
        String randomId = UUID.randomUUID().toString();
        ByteArrayResource avatar = avatarService.generateAvatar(randomId, AVATAR_SIZE);
        ByteArrayResource smallAvatar = avatarService.generateAvatar(randomId, SMALL_AVATAR_SIZE);
        // save to db with different id
        UserProfilePic userProfilePic = savePicToDb(userId);
        //save to minio with db ids (*id*.png)
        avatarService.uploadAvatar(avatar, userProfilePic.getFileId());
        avatarService.uploadAvatar(smallAvatar, userProfilePic.getSmallFileId());
    }

    private UserProfilePic savePicToDb(long userId) {
        String picId = UUID.randomUUID().toString();
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(picId);
        userProfilePic.setSmallFileId(picId + "-small");

        User user = userRepository.findById(userId).orElseThrow();
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        return userProfilePic;
    }
}


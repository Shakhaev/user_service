package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserRegisterDto;
import school.faang.user_service.dto.user.UserResponseRegisterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.user.UserRegisterMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final int AVATAR_SIZE = 64;
    private static final int SMALL_AVATAR_SIZE = 32;

    private final UserRepository userRepository;
    private final UserRegisterMapper userRegisterMapper;
    private final AvatarService avatarService;

    @Override
    public UserResponseRegisterDto registerUser(UserRegisterDto dto) {
        User user = userRegisterMapper.toEntity(dto);
        Pair<String, String> avatars = saveAvatarsToMinio(user);
        User savedUser = saveAvatarsIdsToDb(user, avatars);
        return userRegisterMapper.toDto(userRepository.save(savedUser));
    }

    @Override
    public Pair<String, String> saveAvatarsToMinio(User user) {
        String randomId = UUID.randomUUID().toString();
        ByteArrayResource avatar = avatarService.getAvatarFromDiceBear(randomId, AVATAR_SIZE);
        ByteArrayResource smallAvatar = avatarService.getAvatarFromDiceBear(randomId, SMALL_AVATAR_SIZE);

        String avatarId = avatarService.uploadAvatar(avatar);
        String avatarSmallId = avatarService.uploadAvatar(smallAvatar);

        return Pair.of(avatarId, avatarSmallId);
    }

    private User saveAvatarsIdsToDb(User user, Pair<String, String> avatars) {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(avatars.getLeft());
        userProfilePic.setSmallFileId(avatars.getRight());

        user.setUserProfilePic(userProfilePic);
        return userRepository.save(user);
    }
}


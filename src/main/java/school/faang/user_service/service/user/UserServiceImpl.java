package school.faang.user_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserRegisterDto;
import school.faang.user_service.dto.user.UserResponseRegisterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AvatarService avatarService;

    @Override
    @Transactional
    public UserResponseRegisterDto registerUser(UserRegisterDto dto) {
        User user = userMapper.toEntity(dto);
        Pair<String, String> avatars = avatarService.saveAvatarsToMinio(user);
        User savedUser = saveAvatarsIdsToDb(user, avatars);
        return userMapper.toDto(savedUser);
    }

    private User saveAvatarsIdsToDb(User user, Pair<String, String> avatars) {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(avatars.getLeft());
        userProfilePic.setSmallFileId(avatars.getRight());

        user.setUserProfilePic(userProfilePic);
        return userRepository.save(user);
    }
}


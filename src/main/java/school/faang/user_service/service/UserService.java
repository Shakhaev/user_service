package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.filters.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.profilePicture.AvatarService;
import school.faang.user_service.service.profilePicture.RandomAvatarService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AvatarService avatarService;
    private final RandomAvatarService randomAvatarService;
    private final CountryRepository countryRepository;


    @Transactional
    public void deactivateUser(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));

        mentorshipService.deactivateMentorship(user.getId());
        goalService.deactivateGoalsByUser(user.getId());
        eventService.deactivateEventsByUser(user.getId());
    }

    public boolean isUserExist(Long userId) {
        return userRepository.existsById(userId);
    }

    @Transactional
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        Stream<User> users = userRepository.findPremiumUsers();
        return users.filter(user -> userFilters.stream().filter(filter -> filter.isApplicable(userFilterDto))
                        .anyMatch(filter -> filter.filterEntity(user, userFilterDto)))
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public UserDto createUser(@Valid CreateUserDto createUserDto) {
        validateNewUser(createUserDto);
        String encodedPassword = passwordEncoder.encode(createUserDto.getPassword());

        UserProfilePic userProfilePic = setProfilePicture(createUserDto);
        User user = userMapper.toEntity(createUserDto);
        user.setCountry(countryRepository.findById(createUserDto.getCountryId())
                .orElseThrow(() -> new IllegalArgumentException("Страны с таким id "
                        + createUserDto.getCountryId()
                        + " не существует")));
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    private void validateNewUser(CreateUserDto createUserDto) {
        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }
        if (userRepository.existsByUsername(createUserDto.getUsername())) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }
        if (!Objects.equals(createUserDto.getPassword(), createUserDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }
    }

    private UserProfilePic setProfilePicture(CreateUserDto createUserDto) {
        String profilePicUrl;
        String thumbnailUrl = null;

        if (createUserDto.getProfilePic() != null && !createUserDto.getProfilePic().isEmpty()) {
            MultipartFile profilePic = createUserDto.getProfilePic();
            try {
                profilePicUrl = avatarService.uploadAvatar(profilePic);
                thumbnailUrl = avatarService.uploadThumbnailAvatar(profilePic);
            } catch (IOException e) {
                throw new BusinessException("Ошибка при загрузке аватарки");
            }
        } else {
            try {
                String seed = createUserDto.getUsername();
                profilePicUrl = randomAvatarService.generateAndUploadAvatar(seed);
            } catch (IOException e) {
                throw new BusinessException("Ошибка при генерации и загрузке аватарки");
            }
        }
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(profilePicUrl);
        userProfilePic.setSmallFileId(thumbnailUrl);

        return userProfilePic;
    }
}

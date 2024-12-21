package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.kafka.KafkaTopicsProps;
import school.faang.user_service.dto.user.UserProfileCreateDto;
import school.faang.user_service.dto.user.UserProfileResponseDto;
import school.faang.user_service.dto.user.UserProfileUpdateDto;
import school.faang.user_service.dto.user.UserSearchResponse;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.message.event.reindex.user.UserDocument;
import school.faang.user_service.message.producer.KeyedMessagePublisher;
import school.faang.user_service.model.jpa.Country;
import school.faang.user_service.model.jpa.User;
import school.faang.user_service.repository.jpa.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final UserMapper userMapper;
    private final CountryService countryService;
    private final KeyedMessagePublisher keyedMessagePublisher;
    private final KafkaTopicsProps kafkaTopicsProps;

    public UserSearchResponse getUserById(long id) {
        User user = userRepo.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        return userMapper.toSearchResponse(user);
    }

    public User findUserById(long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public List<User> getAllUsersByIds(List<Long> ids) {
        return userRepo.findAllById(ids);
    }

    public UserProfileResponseDto updateUserProfile(Long userId, UserProfileUpdateDto userProfileUpdateDto) {
        User user = updateUserState(userId, userProfileUpdateDto);
        userRepo.save(user);
        reindexUser(user);

        keyedMessagePublisher.send(
                kafkaTopicsProps.getUpdateUserTopic().getName(),
                userId.toString(),
                userMapper.toSearchResponse(user));

        return userMapper.toUserProfileResponseDto(user);
    }

    @NotNull
    private User updateUserState(Long userId, UserProfileUpdateDto userProfileUpdateDto) {
        User user = findUserById(userId);
        user.setUsername(userProfileUpdateDto.username());
        user.setEmail(userProfileUpdateDto.email());
        user.setAboutMe(userProfileUpdateDto.aboutMe());

        mapIdToCountry(userProfileUpdateDto.countryId(), user);

        user.setCity(userProfileUpdateDto.city());
        user.setExperience(userProfileUpdateDto.experience());
        return user;
    }

    public UserProfileResponseDto createUserProfile(UserProfileCreateDto userProfileCreateDto) {
        User user = userMapper.toEntity(userProfileCreateDto);
        mapIdToCountry(userProfileCreateDto.countryId(), user);
        userRepo.save(user);
        reindexUser(user);
        return userMapper.toUserProfileResponseDto(user);
    }

    private void mapIdToCountry(Long countryId, User user) {
        Country country = countryService.findCountryById(countryId)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "id", countryId));
        user.setCountry(country);
    }

    private void reindexUser(User user) {
        UserDocument userDocument = userMapper.toUserDocument(user);
        keyedMessagePublisher.send(
                kafkaTopicsProps.getUserIndexingTopic().getName(),
                userDocument.getResourceId().toString(),
                userDocument);
    }

    public int calculateGridSize(long partitionSize) {
        long totalUsers = userRepo.count();
        return (int) Math.ceil((double) totalUsers / partitionSize);
    }

    public Long findMaxId() {
        return userRepo.findMaxId();
    }

    public Long findMinId() {
        return userRepo.findMinId();
    }
}
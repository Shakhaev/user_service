package school.faang.user_service.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PromotionServiceClient;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.UserRegisterRequest;
import school.faang.user_service.dto.UserRegisterResponse;
import school.faang.user_service.dto.promotion.UserPromotionRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.MinioSaveException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.exception.UserAlreadyExistsException;
import school.faang.user_service.filters.interfaces.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.external.AvatarService;
import school.faang.user_service.service.external.MinioStorageService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.util.ConverterUtil;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static school.faang.user_service.config.KafkaConstants.PAYMENT_PROMOTION_TOPIC;
import static school.faang.user_service.config.KafkaConstants.USER_KEY;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConverterUtil converterUtil;
    private final PromotionServiceClient promotionServiceClient;
    private final UserMapper userMapper;
    private final AvatarService avatarService;
    private final MinioStorageService minioStorageService;
    private final List<UserFilter> userFilters;

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.userNotFoundException(id));
    }

    @Transactional
    public void deactivateUser(Long userId) {
        User user = findById(userId);

        List<Goal> goals = user.getGoals();
        goals.forEach(goal -> goalService.removeUserFromGoal(goal, userId));

        LocalDateTime currentTime = LocalDateTime.now();
        List<Event> neededToRemove = new ArrayList<>();
        user.getOwnedEvents().forEach(event -> {
            if (event.getStartDate().isAfter(currentTime)) { //Если ивент ещё не начался - удаляем
                neededToRemove.add(event);
                eventService.removeEvent(event.getId()); //Удаление ивентов из БД
            }
        });
        user.setOwnedEvents(user.getOwnedEvents().stream()
                .filter(event -> !neededToRemove.contains(event)).toList()); // Удаление ивентов из списка пользователя


        user.setActive(false);
        userRepository.save(user);

        mentorshipService.removeMentorship(userId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getPremiumUsers(UserFilterDto filterDto) {
        List<User> users = userRepository.findPremiumUsers().toList();
        return userFilters.stream()
                .filter(filter -> filter.isAcceptable(filterDto))
                .flatMap(filter -> filter.accept(users.stream(), filterDto))
                .map(userMapper::toDto)
                .toList();
    }

    public void userPromotion(UserPromotionRequest userPromotionRequest) {
        findById(userPromotionRequest.userId());
        String message = converterUtil.convertToJson(userPromotionRequest);
        kafkaTemplate.send(PAYMENT_PROMOTION_TOPIC, USER_KEY, message);
    }

    public List<UserDto> getPromotionUsers() {
        List<Long> userIds = promotionServiceClient.getPromotionUsers();
        return userIds.stream()
                .map(userId -> userMapper.toDto(findById(userId)))
                .toList();
    }

    public UserRegisterResponse register(@Valid UserRegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("username: " + request.username() + " is busy");
        }
        String avatar = avatarService.getRandomAvatar().block();
        String avatarId = UUID.randomUUID().toString();

        try {
            minioStorageService.saveFile(avatar, avatarId);
        } catch (Exception e) {
            throw new MinioSaveException("Minio error save file" + e.getMessage());
        }

        User user = userMapper.toEntity(request);
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(avatarId);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        return userMapper.toUserRegisterResponse(user);
    }

    public byte[] getUserAvatar(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw ResourceNotFoundException.userNotFoundException(userId);
        }

        String fileId = userRepository.getUserProfileFileId(userId)
                .orElseThrow(() -> ResourceNotFoundException.userAvatarNotFoundException(userId));


        try {
            String avatar = minioStorageService.getFile(fileId);
            return avatar.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new MinioSaveException("Minio error save file" + e.getMessage());
        }
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.userNotFoundException(userId));
    }

}

package school.faang.user_service.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserRegisterRequest;
import school.faang.user_service.dto.UserRegisterResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.exception.UserAlreadyExistsException;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;

    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Не удалось получить пользователя с id " + id));
    }

    @Transactional
    public void deactivateUser(Long userId) {
        User user = getUser(userId);

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

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.userNotFoundException(id));
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
}

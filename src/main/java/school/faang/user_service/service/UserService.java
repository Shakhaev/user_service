package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validation.UserValidation;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;
    private final UserValidation userValidation;

    @Autowired
    public UserService(UserRepository userRepository, EventRepository eventRepository, GoalRepository goalRepository,
                       MentorshipService mentorshipService, UserMapper userMapper, UserValidation userValidation) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.goalRepository = goalRepository;
        this.mentorshipService = mentorshipService;
        this.userMapper = userMapper;
        this.userValidation = userValidation;
    }

    public UserDto deactivate(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        removeEvents(userId);
        removeGoals(userId);
        user.setActive(false);
        return userMapper.toUserDto(userRepository.save(user));
    }

    public void removeMenteeAndGoals(Long userId) {
        mentorshipService.removeMenteeGoals(userId);
        mentorshipService.removeMenteeFromUser(userId);
    }

    public void deleteInactiveUsers() {
        userRepository.findAll().stream()
                .filter(user -> !user.isActive())
                .filter(user -> user.getUpdatedAt().plusDays(90).isBefore(LocalDateTime.now()))
                .forEach(userRepository::delete);
    }

    private void removeEvents(Long userId) {
        userValidation.validateUserIdExist(userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!user.getOwnedEvents().isEmpty()) {
            user.getOwnedEvents().forEach(event -> {
                event.setStatus(EventStatus.CANCELED);
                eventRepository.save(event);
            });
        }
    }

    private void removeGoals(Long userId) {
        userValidation.validateUserIdExist(userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!user.getGoals().isEmpty()) {
            user.getGoals().forEach(goal -> {
                goal.getUsers().remove(user);
                if (goal.getUsers().isEmpty()) {
                    goalRepository.delete(goal);
                }
            });
        }
    }

    public void isUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователя с id " + userId + " не существует");
        }
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public List<User> getAllByIds(@NotNull List<Long> userIds) {
        return userRepository.findAllById(userIds);
    }

    public User getById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Невозможно получить пользователя"));
    }
}

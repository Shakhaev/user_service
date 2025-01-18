package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validation.UserValidation;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;
    private final UserValidation userValidation;

    @Autowired
    public UserService(UserRepository userRepository, EventRepository eventRepository, GoalRepository goalRepository
            , MentorshipService mentorshipService, UserMapper userMapper, UserValidation userValidation) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.goalRepository = goalRepository;
        this.mentorshipService = mentorshipService;
        this.userMapper = userMapper;
        this.userValidation = userValidation;
    }

    public UserDto deactivate(Long userId) {
        User user = userRepository.findById(userId).get();
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
        User user = userRepository.findById(userId).get();
        if (!user.getOwnedEvents().isEmpty()) {
            user.getOwnedEvents().forEach(event -> {
                event.setStatus(EventStatus.CANCELED);
                eventRepository.save(event);
            });
        }
    }

    private void removeGoals(Long userId) {
        userValidation.validateUserIdExist(userId);
        User user = userRepository.findById(userId).get();
        if (!user.getGoals().isEmpty()) {
            user.getGoals().forEach(goal -> {
                goal.getUsers().remove(user);
                if (goal.getUsers().isEmpty()) {
                    goalRepository.delete(goal);
                }
            });
        }
    }
}
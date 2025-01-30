package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import jakarta.persistence.EntityNotFoundException;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.validator.UserValidator;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GoalService goalService;
    private final EventService eventService;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final MentorshipService mentorshipService;

    public UserDto findUserById(Long id) {
        return userMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id not found")));
    }

    @Transactional
    public void deactivateUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        userValidator.checkUserExists(optionalUser);
        User user = optionalUser.get();

        goalService.completeGoalsByUser(id);
        eventService.completeEventsByUser(id);
        mentorshipService.stopMentorship(user);

        user.setActive(false);
        userRepository.save(user);
    }
}

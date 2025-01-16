package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;

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
}

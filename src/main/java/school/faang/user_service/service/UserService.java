package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.goal.GoalService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Не удалось получить пользователя с id " + id));
    }

    public void deactivateUser(Long userId) {
        User user = getUser(userId);

        List<Goal> goals = user.getGoals();
        goals.forEach(goal -> goalService.removeUserFromGoal(goal, userId));

        LocalDateTime currentTime = LocalDateTime.now();
        user.getOwnedEvents().forEach(event -> {
            if (event.getStartDate().isAfter(currentTime)) { //Если ивент ещё не начался - удаляем
                eventService.removeEvent(event.getId());
            }
        });

        user.setActive(false);
        userRepository.save(user);

        mentorshipService.removeMentorship(userId);
    }

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.userNotFoundException(id));
    }
}
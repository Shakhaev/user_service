package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;

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

}

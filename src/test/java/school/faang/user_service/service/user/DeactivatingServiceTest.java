package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.ResponseDeactivateDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeactivatingServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private MentorshipService mentorsService;
    @Mock
    private EventRepository eventRepository;


    @Test
    void testDeactivateUserWhenUserIsActive() {
        long userId = 23L;

        User user = User.builder().id(23L).username("Sasha").active(true).build();
        User user2 = User.builder().id(32L).username("Pavel").active(false).build();

        List<User> users = List.of(user);
        List<User> usersFalse = List.of(user2, user);

        Goal goal1 = Goal.builder().id(1L).mentor(user).users(users).build();
        Goal goal2 = Goal.builder().id(2L).mentor(user2).users(usersFalse).build();

        Stream<Goal> goalStream = Stream.of(goal1, goal2);


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goalStream);

        ResponseDeactivateDto response = userService.deactivateUser(userId);

        assertFalse(user.isActive());
        verify(userRepository).save(user);
        assertEquals("User was successfully deactivated", response.getMessage());
    }
}

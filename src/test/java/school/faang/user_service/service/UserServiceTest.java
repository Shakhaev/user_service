package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.goal.GoalService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalService goalService;
    @Mock
    private EventService eventService;
    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    private UserService service;

    private User user;
    @BeforeEach
    public void init() {
        user = User.builder()
                .id(1L)
                .username("Bob")
                .active(true)
                .goals(List.of(
                        Goal.builder().id(1L).build(),
                        Goal.builder().id(2L).build(),
                        Goal.builder().id(3L).build(),
                        Goal.builder().id(4L).build()
                ))
                .ownedEvents(List.of(
                        Event.builder().id(1L).startDate(LocalDateTime.now().plusDays(10)).build(),
                        Event.builder().id(2L).startDate(LocalDateTime.now().plusDays(10)).build(),
                        Event.builder().id(3L).startDate(LocalDateTime.now().plusDays(10)).build()
                ))
                .build();
    }
    @Test
    public void deactivateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        service.deactivateUser(1L);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        Assertions.assertEquals(savedUser.getId(), user.getId());
        Assertions.assertTrue(savedUser.getOwnedEvents().isEmpty());
    }
}

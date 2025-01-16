package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private UserService userService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deactivateUser_userExist_callsAllServices() {
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        userService.deactivateUser(userId);

        verify(mentorshipService).deactivateMentorship(userId);
        verify(goalService).deactivateGoalsByUser(userId);
        verify(eventService).deactivateEventsByUser(userId);
    }

    @Test
    void deactivateUser_userNotFound_throwsException() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                        userService.deactivateUser(userId));

        assertEquals("Пользователь с ID " + userId + " не найден", exception.getMessage());
    }
}

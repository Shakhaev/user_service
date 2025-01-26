package school.faang.user_service.service;

import jakarta.persistence.EnumType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.goal.GoalStatus.ACTIVE;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private MentorshipService mentorshipService;

    @Spy
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User activeUsers;
    private User inactiveUser;
    private User activeUser;
    private Event activeEvent;

    @BeforeEach
    public void setUp() {

        activeUsers = new User();
        activeUsers.setActive(true);
        activeUsers.setUpdatedAt(LocalDateTime.now());

        inactiveUser = new User();
        inactiveUser.setActive(false);
        inactiveUser.setUpdatedAt(LocalDateTime.now().minusDays(91));

        activeUser = User.builder()
                .id(1L)
                .active(true)
                .updatedAt(LocalDateTime.now())
                .ownedEvents(new ArrayList<>())
                .goals(new ArrayList<>())
                .build();
    }

    @Test
    void shouldSuccessWhenUserExists() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        userService.isUserExists(anyLong());
        verify(userRepository).existsById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void shouldThrowBusinessExceptionWhenUserNotExists() {
        long nonExistingUserId = 123L;
        when(userRepository.existsById(nonExistingUserId)).thenReturn(false);

        assertThrows(BusinessException.class,
                () -> userService.isUserExists(nonExistingUserId),
                "Пользователя с id " + nonExistingUserId + " не существует");
    }

    @Test
    void shouldSuccessSaveUser() {
        User user = new User();
        userService.saveUser(user);

        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testRemoveMenteeAndGoals() {
        Long userId = 123L;
        userService.removeMenteeAndGoals(userId);
        verify(mentorshipService, times(1)).removeMenteeGoals(userId);
        verify(mentorshipService, times(1)).removeMenteeFromUser(userId);
    }

    @Test
    void testNotDeleteActiveUsers() {
        when(userRepository.findAll()).thenReturn(List.of(activeUsers));

        userService.deleteInactiveUsers();

        verify(userRepository, never()).delete(activeUsers);
    }

    @Test
    void testDeleteUsersAfterDeactivationPeriod() {

        when(userRepository.findAll()).thenReturn(List.of(inactiveUser));

        userService.deleteInactiveUsers();

        verify(userRepository, times(1)).delete(inactiveUser);
    }

    @Test
    void testDeactivate() {

        when(userRepository.findById(activeUser.getId())).thenReturn(Optional.of(activeUser));
        when(userRepository.existsById(activeUser.getId())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userService.deactivate(activeUser.getId());

        assertNotNull(result);
        assertFalse(result.isActive());
        verify(userRepository).findById(activeUser.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDeactivate_UserNotFound() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deactivate(userId));
        verify(userRepository).findById(userId);
    }
}
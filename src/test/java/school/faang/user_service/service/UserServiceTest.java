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

    private static final Long USER_ID = 2L;
    private static final Long UNREAL_USER_ID = 99L;
    private static final int INACTIVATION_PERIOD_DAYS = 91;

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
    private UserDto userDto;

    @BeforeEach
    public void setUp() {

        activeUsers = new User();
        activeUsers.setActive(true);
        activeUsers.setUpdatedAt(LocalDateTime.now());

        inactiveUser = new User();
        inactiveUser.setActive(false);
        inactiveUser.setUpdatedAt(LocalDateTime.now().minusDays(INACTIVATION_PERIOD_DAYS));

        activeUser = User.builder()
                .id(1L)
                .active(true)
                .updatedAt(LocalDateTime.now())
                .ownedEvents(new ArrayList<>())
                .goals(new ArrayList<>())
                .build();

        userDto = new UserDto();
        userDto.setId(USER_ID);

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

        assertThrows(EntityNotFoundException.class,
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
        userService.removeMenteeAndGoals(USER_ID);
        verify(mentorshipService, times(1)).removeMenteeGoals(USER_ID);
        verify(mentorshipService, times(1)).removeMenteeFromUser(USER_ID);
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
        when(userMapper.toUserDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.deactivate(activeUser.getId());

        assertNotNull(result);
        assertFalse(result.isActive());
        verify(userRepository, times(3)).findById(activeUser.getId());

    }

    @Test
    void testDeactivate_UserNotFound() {
        when(userRepository.findById(UNREAL_USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deactivate(UNREAL_USER_ID));
        verify(userRepository).findById(UNREAL_USER_ID);
    }
}
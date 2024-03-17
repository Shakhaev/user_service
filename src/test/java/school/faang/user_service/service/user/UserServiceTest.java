package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.validation.user.UserValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private UserFilter userFilter;
    private MentorshipService mentorshipService;
    private EventService eventService;
    private GoalService goalService;
    private UserValidator userValidator;

    private User user;
    private UserDto userDto;
    private User mentee;
    private Goal goal;
    private Event event;
    private User premiumUser;
    private UserDto premiumUserDto;


    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("Valid username")
                .email("valid@email.com")
                .phone("+71234567890")
                .active(true)
                .build();
        userDto = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .active(user.isActive())
                .build();
        mentee = User.builder()
                .id(2L)
                .mentors(new ArrayList<>(List.of(user)))
                .build();
        goal = Goal.builder()
                .id(3L)
                .mentor(user)
                .status(GoalStatus.ACTIVE)
                .users(new ArrayList<>(List.of(user)))
                .build();
        event = Event.builder()
                .id(4L)
                .owner(user)
                .status(EventStatus.PLANNED)
                .build();
        user.setMentees(new ArrayList<>(List.of(mentee)));
        user.setGoals(new ArrayList<>(List.of(goal)));
        user.setOwnedEvents(new ArrayList<>(List.of(event)));
        premiumUser = User.builder()
                .id(1L)
                .username("Valid username")
                .email("valid@email.com")
                .phone("+71234567890")
                .premium(new Premium())
                .build();
        premiumUserDto = UserDto.builder()
                .id(premiumUser.getId())
                .username(premiumUser.getUsername())
                .email(premiumUser.getEmail())
                .phone(premiumUser.getPhone())
                .isPremium(true)
                .build();
        userRepository = mock(UserRepository.class);
        mentorshipService = mock(MentorshipService.class);
        eventService = mock(EventService.class);
        goalService = mock(GoalService.class);
        userMapper = mock(UserMapper.class);
        userFilter = mock(UserFilter.class);
        userService = new UserService(userRepository, mentorshipService, eventService, goalService,
                userMapper, List.of(userFilter), userValidator);
    }

    @Test
    void getPremiumUsers_UsersFoundAndFiltered_ThenReturnedAsDto() {
        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(premiumUser));
        when(userFilter.isApplicable(any(UserFilterDto.class))).thenReturn(true);
        doNothing().when(userFilter).apply(anyList(), any(UserFilterDto.class));
        when(userMapper.toDto(List.of(premiumUser))).thenReturn(List.of(premiumUserDto));

        userService.getPremiumUsers(new UserFilterDto());

        verify(userRepository, times(1)).findPremiumUsers();
        verify(userMapper, times(1)).toDto(List.of(premiumUser));
    }

    @Test
    void deactivateUser_UserIsDeactivatedAndSavedToDb_GoalsAndEventsAlsoDeleted() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.deactivateUser(user.getId());

        assertAll(
                () -> verify(mentorshipService, times(1)).
                        deleteMentorForAllHisMentees(user.getId(), List.of(mentee)),
                () -> verify(goalService, times(1)).deleteGoal(goal.getId()),
                () -> verify(eventService, times(1)).deleteEvent(event.getId()),
                () -> verify(userRepository, times(1)).save(user),
                () -> verify(userMapper, times(1)).toDto(user),
                () -> assertFalse(user.isActive()),
                () -> assertEquals(Collections.emptyList(), goal.getUsers()),
                () -> assertEquals(Collections.emptyList(), user.getMentees()),
                () -> assertEquals(Collections.emptyList(), user.getGoals()),
                () -> assertEquals(Collections.emptyList(), user.getOwnedEvents())
        );
    }

    @Test
    void getUser_UserFound_ThenReturnedAsDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        userService.getUser(user.getId());

        assertAll(
                () -> verify(userRepository, times(1)).findById(user.getId()),
                () -> verify(userMapper, times(1)).toDto(user)
        );
    }

    @Test
    void getUser_UserNotFound_ShouldThrowEntityNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                userService.getUser(5L));
    }

    @Test
    void getUsersByIds_UsersFound_ThenReturnedAsDto() {
        when(userRepository.findAllById(List.of(user.getId()))).thenReturn((List.of(user)));
        when(userMapper.toDto(List.of(user))).thenReturn(List.of(userDto));

        userService.getUsersByIds(List.of(user.getId()));

        assertAll(
                () -> verify(userRepository, times(1)).findAllById(List.of(user.getId())),
                () -> verify(userMapper, times(1)).toDto(List.of(user))
        );
    }

    @Test
    void getUsersByIds_UsersNotFound_ShouldThrowEntityNotFoundException() {
        when(userRepository.findAllById(List.of(589123098L))).thenReturn(Collections.emptyList());

        assertThrows(EntityNotFoundException.class, () ->
                userService.getUsersByIds(List.of(589123098L)));
    }
}

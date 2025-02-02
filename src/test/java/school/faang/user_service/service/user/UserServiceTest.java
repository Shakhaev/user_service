package school.faang.user_service.service.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.BadRequestException;
import school.faang.user_service.filters.user.UserFilter;
import school.faang.user_service.mapper.DeactivatedUserMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.adapter.EventParticipationRepositoryAdapter;
import school.faang.user_service.repository.adapter.EventRepositoryAdapter;
import school.faang.user_service.repository.adapter.GoalRepositoryAdapter;
import school.faang.user_service.repository.adapter.UserRepositoryAdapter;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private UserService userService;

    private List<UserFilter> userFilters;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private DeactivatedUserMapper deactivatedUserMapper;
    @Mock
    private UserRepositoryAdapter userRepositoryAdapter;
    @Mock
    private GoalRepositoryAdapter goalRepositoryAdapter;
    @Mock
    private EventRepositoryAdapter eventRepositoryAdapter;
    @Mock
    private EventParticipationRepositoryAdapter eventParticipationRepositoryAdapter;
    @Mock
    private MentorshipService mentorshipService;

    @BeforeEach
    void init() {
        UserFilter mockFirstUserFilter = Mockito.mock(UserFilter.class);
        UserFilter mockSecondUserFilter = Mockito.mock(UserFilter.class);
        userFilters = List.of(mockFirstUserFilter, mockSecondUserFilter);

        userService = new UserService(userRepository, userFilters, userMapper, deactivatedUserMapper,
                userRepositoryAdapter, goalRepositoryAdapter, eventRepositoryAdapter,
                eventParticipationRepositoryAdapter, mentorshipService);
    }

    @Test
    void testGetPremiumUsers() {
        User user1 = new User();
        User user2 = new User();

        UserFilterDto userFilterDto = new UserFilterDto();

        UserDto firstUserDto = new UserDto();

        userFilterDto.setCityPattern("Moscow");
        userFilterDto.setNamePattern("Maxim");

        List<User> users = List.of(user1, user2);

        Mockito.when(userFilters.get(0).apply(userRepository.findPremiumUsers().toList(), userFilterDto))
                .thenReturn(users);
        Mockito.when(userFilters.get(0).isApplicable(userFilterDto)).thenReturn(true);
        Mockito.when(userMapper.toDto(user1)).thenReturn(firstUserDto);

        userService.getPremiumUsers(userFilterDto);

        Mockito.verify(userFilters.get(0), Mockito.times(1)).isApplicable(userFilterDto);
        ArgumentCaptor<List<User>> listUsers = ArgumentCaptor.forClass(List.class);
        Mockito.verify(userFilters.get(0), Mockito.times(1)).apply(listUsers.capture(),
                Mockito.eq(userFilterDto));
    }

    @Test
    void testDeactivateUserThrowBadRequestException() {
        long deactivatedUserId = 1L;

        User deactivatedUser = new User();
        deactivatedUser.setId(deactivatedUserId);
        deactivatedUser.setActive(false);

        Mockito.when(userRepositoryAdapter.getById(deactivatedUserId)).thenReturn(deactivatedUser);
        Assertions.assertThrows(BadRequestException.class,
                () -> userService.deactivateUser(deactivatedUserId));
    }

    @Test
    void testDeactivateUserSuccessful() {
        long userId = 1L;

        long event1Id = 1L;
        long event2Id = 2L;

        User user = new User();
        user.setId(userId);
        user.setActive(true);

        List<User> userList = new ArrayList<>();
        userList.add(user);

        Goal userGoal = new Goal();
        userGoal.setId(1L);
        userGoal.setUsers(userList);

        List<Goal> goalList = new ArrayList<>();
        goalList.add(userGoal);
        user.setGoals(goalList);

        Event userEvent1 = new Event();
        userEvent1.setId(event1Id);
        Event userEvent2 = new Event();
        userEvent2.setId(event2Id);

        List<Event> eventList = new ArrayList<>();
        eventList.add(userEvent1);
        eventList.add(userEvent2);

        user.setOwnedEvents(eventList);

        Mockito.when(userRepositoryAdapter.getById(userId)).thenReturn(user);

        Mockito.doNothing().when(goalRepositoryAdapter).delete(userGoal);
        Mockito.doNothing().when(goalRepositoryAdapter).removeUserGoals(user.getId());
        Mockito.doNothing().when(eventParticipationRepositoryAdapter).unregisterAll(event1Id);
        Mockito.doNothing().when(eventParticipationRepositoryAdapter).unregisterAll(event2Id);
        Mockito.doNothing().when(eventRepositoryAdapter).deleteAll(user.getOwnedEvents());
        Mockito.doNothing().when(mentorshipService).stopMentorship(user);

        userService.deactivateUser(userId);

        Mockito.verify(goalRepositoryAdapter, Mockito.times(1)).delete(userGoal);
        Mockito.verify(goalRepositoryAdapter, Mockito.times(1)).removeUserGoals(user.getId());
        Mockito.verify(eventParticipationRepositoryAdapter, Mockito.times(1))
                .unregisterAll(event1Id);
        Mockito.verify(eventParticipationRepositoryAdapter, Mockito.times(1))
                .unregisterAll(event1Id);
        Mockito.verify(eventRepositoryAdapter, Mockito.times(1)).deleteAll(user.getOwnedEvents());

        Assertions.assertFalse(user.isActive());

        Mockito.verify(mentorshipService, Mockito.times(1)).stopMentorship(user);
        Mockito.verify(deactivatedUserMapper, Mockito.times(1)).toDto(user);
    }
}

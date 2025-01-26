package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.user.UserFilter;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import school.faang.user_service.service.profilePicture.AvatarService;
import school.faang.user_service.service.profilePicture.RandomAvatarService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserRepository userRepository;

    private GoalService goalService;

    private EventService eventService;

    private MentorshipService mentorshipService;

    private List<UserFilter> filter;

    private UserMapperImpl userMapperImpl;

    private UserService userService;

    private User user;

    private PasswordEncoder passwordEncoder;

    private AvatarService avatarService;

    private RandomAvatarService randomAvatarService;

    private CountryRepository countryRepository;

    @BeforeEach
    public void init() {
        userRepository = mock(UserRepository.class);
        goalService = mock(GoalService.class);
        eventService = mock(EventService.class);
        mentorshipService = mock(MentorshipService.class);
        filter = List.of(mock(UserFilter.class));
        userMapperImpl = spy(UserMapperImpl.class);
        passwordEncoder = mock(PasswordEncoder.class);
        avatarService = mock(AvatarService.class);
        randomAvatarService = mock(RandomAvatarService.class);
        countryRepository = mock(CountryRepository.class);

        userService = new UserService(
                userRepository,
                goalService,
                eventService,
                mentorshipService,
                filter,
                userMapperImpl,
                passwordEncoder,
                avatarService,
                randomAvatarService,
                countryRepository
        );


    user = User.builder()
                .id(1L)
                .username("Mark")
                .city("Moscow")
                .build();
    }

    @Test
    void deactivateUser_userExist_callsAllServices() {
        Long userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deactivateUser(userId);

        verify(mentorshipService).deactivateMentorship(userId);
        verify(goalService).deactivateGoalsByUser(userId);
        verify(eventService).deactivateEventsByUser(userId);
    }

    @Test
    void deactivateUser_userNotFound_throwsException() {
        Long userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.deactivateUser(userId));

        assertEquals("Пользователь с ID " + userId + " не найден", exception.getMessage());
    }

    @Test
    void testGetPremiumUsersWithFilter() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setNamePattern("Mark");

        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(user));
        when(filter.get(0).isApplicable(userFilterDto)).thenReturn(true);
        when(filter.get(0).filterEntity(user, userFilterDto)).thenReturn(true);

        List<UserDto> result = userService.getPremiumUsers(userFilterDto);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(user.getUsername(), result.get(0).username());

    }

}

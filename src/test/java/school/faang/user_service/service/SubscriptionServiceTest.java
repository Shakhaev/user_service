package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private UserFilterDto filter;
    private User user1;
    private User user2;

    @Test
    public void shouldFollowUserWhenNotAlreadySubscribed() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);
        subscriptionService.followUser(followerId, followeeId);
        verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionWhenAlreadySubscribed() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                subscriptionService.followUser(followerId, followeeId)
        );

        assertEquals("This subscriber already exists", exception.getMessage());
        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    public void shouldUnfollowUserWhenSubscribed() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);
        subscriptionService.unfollowUser(followerId, followeeId);
        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionWhenUnsubscribed() {
        long followerId = 1L;
        long followeeId = 2L;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                subscriptionService.unfollowUser(followerId, followeeId)
        );

        assertEquals("You are not subscribed to this user", exception.getMessage());
        verify(subscriptionRepository, never()).unfollowUser(anyLong(), anyLong());
    }

    @Test
    public void shouldReturnFollowersCount() {
        long followerId = 1L;
        int expectedCount = 5;

        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(expectedCount);

        int actualCount = subscriptionService.getFollowersCount(followerId);

        assertEquals(expectedCount, actualCount);
        verify(subscriptionRepository, times(1)).findFolloweesAmountByFollowerId(followerId);
    }

    @BeforeEach
    void setUp() {
        filter = new UserFilterDto();
        filter.setNamePattern("JohnDoe");
        filter.setEmailPattern("johndoe@example.com");
        filter.setCityPattern("New York");
        filter.setExperienceMin(1);
        filter.setExperienceMax(10);
        filter.setPage(0);
        filter.setPageSize(10);

        user1 = new User();
        user1.setUsername("JohnDoe");
        user1.setEmail("johndoe@example.com");
        user1.setCity("New York");
        user1.setExperience(2);

        user2 = new User();
        user2.setUsername("JaneSmith");
        user2.setEmail("janesmith@example.com");
        user2.setCity("London");
        user2.setExperience(5);
    }

    @Test
    void shouldFilterFollowersBasedOnCriteria() {

        Stream<User> followers = Stream.of(user1, user2);

        List<User> filteredUsers = subscriptionService.filterUsers(followers, filter);

        assertEquals(1, filteredUsers.size());
        assertEquals("JohnDoe", filteredUsers.get(0).getUsername());
    }

    @Test
    void shouldReturnFollowersAsDtos() {
        long followeeId = 1L;

        User user = new User();
        user.setUsername("JohnDoe");
        user.setEmail("johndoe@example.com");

        UserDto userDto = new UserDto();
        userDto.setUsername("JohnDoe");
        userDto.setEmail("johndoe@example.com");

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(Stream.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> result = subscriptionService.getFollowers(followeeId, filter);

        // Проверяем результат
        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getUsername());
        assertEquals("alice@example.com", result.get(0).getEmail());
        verify(subscriptionRepository, times(1)).findByFolloweeId(followeeId);
        verify(userMapper, times(1)).toDto(user);
    }
}
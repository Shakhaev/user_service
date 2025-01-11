package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.FollowingFeatureDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.UserWasNotFoundException;
import school.faang.user_service.filters.interfaces.UserFilter;
import school.faang.user_service.mapper.UserFollowingMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserFollowingMapper userFollowingMapper;

    @Mock
    private List<UserFilter> filters;

    private static final int RETURN_VALUE = 5;

    @Spy
    private UserFilter userFilter;

    @Test
    void testGetFollowersCount() {
        long followeeId = 1L;
        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(RETURN_VALUE);

        long result = subscriptionService.getFollowersCount(followeeId);

        assertEquals(RETURN_VALUE, result);
        verify(subscriptionRepository, times(1)).findFollowersAmountByFolloweeId(followeeId);
    }

    @Test
    void testFollowUserSuccessfully() {
        FollowingFeatureDto dto = new FollowingFeatureDto(1L, 2L);
        User follower = new User();
        User followee = new User();
        follower.setFollowees(new ArrayList<>());
        followee.setFollowers(new ArrayList<>());

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(followee));
        when(subscriptionRepository.findFollowersAmountByFolloweeId(anyLong())).thenReturn(0);

        subscriptionService.followUser(dto);

        assertTrue(follower.getFollowees().contains(followee));
        assertTrue(followee.getFollowers().contains(follower));
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void testFollowUserFailsWhenAlreadyFollowing() {
        FollowingFeatureDto dto = new FollowingFeatureDto(1L, 2L);
        User follower = new User();
        User followee = new User();
        follower.setFollowees(new ArrayList<>(List.of(followee)));
        followee.setFollowers(new ArrayList<>(List.of(follower)));

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(followee));

        subscriptionService.followUser(dto);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testFollowUserFailsForSameUser() {
        FollowingFeatureDto dto = new FollowingFeatureDto(1L, 1L);

        Exception exception = assertThrows(DataValidationException.class, () -> subscriptionService.followUser(dto));
        assertEquals("Trying to follow to yourself!", exception.getMessage());
    }

    @Test
    void testUnfollowUserSuccessfully() {
        FollowingFeatureDto dto = new FollowingFeatureDto(1L, 2L);
        User follower = new User();
        User followee = new User();
        follower.setFollowees(new ArrayList<>(List.of(followee)));
        followee.setFollowers(new ArrayList<>(List.of(follower)));

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(followee));

        subscriptionService.unfollowUser(dto);

        assertFalse(follower.getFollowees().contains(followee));
        assertFalse(followee.getFollowers().contains(follower));
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void testUnfollowUserFailsWhenNotFollowing() {
        FollowingFeatureDto dto = new FollowingFeatureDto(1L, 2L);
        User follower = new User();
        User followee = new User();
        follower.setFollowees(new ArrayList<>());
        followee.setFollowers(new ArrayList<>());

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(followee));

        Exception exception = assertThrows(DataValidationException.class, () -> subscriptionService.unfollowUser(dto));
        assertEquals("Trying to follow to person not followed!", exception.getMessage());
    }

    @Test
    void testFollowUserThrowsExceptionWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        FollowingFeatureDto dto = new FollowingFeatureDto(1L, 2L);

        Exception exception = assertThrows(UserWasNotFoundException.class, () -> subscriptionService.followUser(dto));
        assertEquals("User was not found with id : 1", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }
}

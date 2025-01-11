package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFollowersCount() {
        long followeeId = 1L;
        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(5);

        long result = subscriptionService.getFollowersCount(followeeId);

        assertEquals(5L, result);
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

        ResponseEntity<Void> response = subscriptionService.followUser(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
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

        ResponseEntity<Void> response = subscriptionService.followUser(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
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

        ResponseEntity<Void> response = subscriptionService.unfollowUser(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
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
    void testFindUserByIdThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserWasNotFoundException.class, () -> subscriptionService.findUserById(1L));
        assertEquals("User was not found with id : 1", exception.getMessage());
    }
}

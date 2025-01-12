package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.FollowingFeatureDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.UserWasNotFoundException;
import school.faang.user_service.mapper.UserFollowingMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

    private static final int RETURN_VALUE = 5;

    @Test
    void getFollowers_ShouldReturnListOfFollowersWhenFollowersExist() {
        long followerId = 1L;
        UserFilterDto filterDto = new UserFilterDto("test", null, null,
                null, null, null,
                null, null,
                0, 10, 0, 10);

        User user = mock(User.class);
        when(subscriptionRepository.findByFollowerId(followerId)).thenReturn(Stream.of(user));
        when(userFollowingMapper.toDto(user)).thenReturn(new UserDto(1L, "username", "email@example.com"));

        List<UserDto> result = subscriptionService.getFollowers(followerId, filterDto);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        verify(subscriptionRepository).findByFollowerId(followerId);
        verify(userFollowingMapper).toDto(user);
    }

    @Test
    void getFollowers_ShouldReturnEmptyListWhenNoFollowersExist() {
        long followerId = 1L;
        UserFilterDto filterDto = new UserFilterDto("test", null, null,
                null, null, null,
                null, null, 0,
                10, 0, 10);

        when(subscriptionRepository.findByFollowerId(followerId)).thenReturn(Stream.empty());

        List<UserDto> result = subscriptionService.getFollowers(followerId, filterDto);

        assertTrue(result.isEmpty());
        verify(subscriptionRepository).findByFollowerId(followerId);
    }

    @Test
    void getFollowees_ShouldReturnListOfFolloweesWhenFolloweesExist() {
        long followeeId = 1L;
        UserFilterDto filterDto = new UserFilterDto("test", null, null,
                null, null, null,
                null, null,
                0, 10, 0, 10);

        User user = mock(User.class);
        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(Stream.of(user));
        when(userFollowingMapper.toDto(user)).thenReturn(new UserDto(1L, "username", "email@example.com"));

        List<UserDto> result = subscriptionService.getFollowees(followeeId, filterDto);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        verify(subscriptionRepository).findByFolloweeId(followeeId);
        verify(userFollowingMapper).toDto(user);
    }

    @Test
    void getFollowees_ShouldReturnEmptyListWhenNoFolloweesExist() {
        long followeeId = 1L;
        UserFilterDto filterDto = new UserFilterDto("test", null,
                null, null, null, null,
                null, null, 0,
                10, 0, 10);

        when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(Stream.empty());

        List<UserDto> result = subscriptionService.getFollowees(followeeId, filterDto);

        assertTrue(result.isEmpty());
        verify(subscriptionRepository).findByFolloweeId(followeeId);
    }

    @Test
    void getFollowersCount_ShouldReturnCorrectCountWhenFollowersExist() {
        long followeeId = 1L;
        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(RETURN_VALUE);

        long result = subscriptionService.getFollowersCount(followeeId);

        assertEquals(RETURN_VALUE, result);
        verify(subscriptionRepository, times(1)).findFollowersAmountByFolloweeId(followeeId);
    }

    @Test
    void followUser_ShouldAddFolloweeToFollowerWhenUsersExist() {
        FollowingFeatureDto dto = new FollowingFeatureDto(1L, 2L);
        User follower = new User();
        User followee = new User();
        follower.setFollowees(new ArrayList<>());
        followee.setFollowers(new ArrayList<>());

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(followee));

        subscriptionService.followUser(dto);

        assertTrue(follower.getFollowees().contains(followee));
        assertTrue(followee.getFollowers().contains(follower));
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void followUser_ShouldThrowDataValidationExceptionWhenUserTriesToFollowSelf() {
        FollowingFeatureDto dto = new FollowingFeatureDto(1L, 1L);

        Exception exception = assertThrows(DataValidationException.class, () -> subscriptionService.followUser(dto));
        assertEquals("Trying to follow to yourself!", exception.getMessage());
    }

    @Test
    void unfollowUser_ShouldRemoveFolloweeFromFollowerWhenRelationshipExists() {
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
    void unfollowUser_ShouldThrowDataValidationExceptionWhenNotFollowingUser() {
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
    void followUser_ShouldThrowUserWasNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        FollowingFeatureDto dto = new FollowingFeatureDto(1L, 2L);

        Exception exception = assertThrows(UserWasNotFoundException.class, () -> subscriptionService.followUser(dto));
        assertEquals("User was not found with id : 1", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }
}

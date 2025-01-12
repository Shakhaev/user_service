package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;

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

    @Test
    public void shouldFollowUserWhenNotAlreadySubscribed() {
        long followerId = 11;
        long followeeId = 21;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);
        subscriptionService.followUser(followerId, followeeId);
        verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionWhenAlreadySubscribed() {
        long followerId = 11;
        long followeeId = 21;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                subscriptionService.followUser(followerId, followeeId)
        );

        assertEquals("This subscriber already exists", exception.getMessage());
        verify(subscriptionRepository, never()).followUser(anyLong(), anyLong());
    }

    @Test
    public void shouldUnfollowUserWhenSubscribed() {
        long followerId = 11;
        long followeeId = 21;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);
        subscriptionService.unfollowUser(followerId, followeeId);
        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionWhenUnsubscribed() {
        long followerId = 11;
        long followeeId = 21;

        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                subscriptionService.unfollowUser(followerId, followeeId)
        );

        assertEquals("You are not subscribed to this user", exception.getMessage());
        verify(subscriptionRepository, never()).unfollowUser(anyLong(), anyLong());
    }
}
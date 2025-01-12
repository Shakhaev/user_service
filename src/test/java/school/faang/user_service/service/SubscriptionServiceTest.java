package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    public void shouldFollowUserWhenNotAlreadySubscribed() {
        long followerId = 11;
        long followeeId = 21;

        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);
        subscriptionService.followUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionWhenAlreadySubscribed() {
        long followerId = 11;
        long followeeId = 21;

        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                subscriptionService.followUser(followerId, followeeId)
        );

        assertEquals("This subscriber already exists", exception.getMessage());
        Mockito.verify(subscriptionRepository, Mockito.never()).followUser(Mockito.anyLong(), Mockito.anyLong());
     }

    @Test
    public void shouldUnfollowUserWhenSubscribed() {
        long followerId = 11;
        long followeeId = 21;

        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);
        subscriptionService.unfollowUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    public void shouldThrowExceptionWhenUnsubscribed() {
        long followerId = 11;
        long followeeId = 21;

        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                subscriptionService.unfollowUser(followerId, followeeId)
        );

        assertEquals("You are not subscribed to this user", exception.getMessage());
        Mockito.verify(subscriptionRepository, Mockito.never()).unfollowUser(Mockito.anyLong(), Mockito.anyLong());
    }
}
package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    SubscriptionRepository subscriptionRepository;
    @InjectMocks
    SubscriptionService subscriptionService;

    @Test
    void followUser() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(4, 5))
                .thenReturn(false);
        Assertions.assertDoesNotThrow(() -> subscriptionService.followUser(4, 5));
        Mockito.verify(subscriptionRepository).existsByFollowerIdAndFolloweeId(4, 5);
    }

    @Test
    void followSameUser() {
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(4, 4),
                "FollowerId 4 and FolloweeId 4 cannot be the same");
        Mockito.verify(subscriptionRepository, Mockito.never()).existsByFollowerIdAndFolloweeId(4, 4);
    }

    @Test
    void followIsExist() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(4, 5))
                .thenReturn(true);
        Assertions.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(4, 5),
                "This subscription (4 - 5) already exists");
        Mockito.verify(subscriptionRepository).existsByFollowerIdAndFolloweeId(4, 5);
    }

    @Test
    void unfollowUser() {
        Assertions.assertDoesNotThrow(() -> subscriptionService.unfollowUser(4, 5));
        Mockito.verify(subscriptionRepository).unfollowUser(4, 5);
    }

    @Test
    void getFollowers() {

    }

    @Test
    void getFollowersCount() {
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(Mockito.anyLong()))
                .thenReturn(77);
        int expectedCount = 77;
        int actualCount = subscriptionService.getFollowingCount(3L);
        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void getFollowersCountNegative() {
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(Mockito.anyLong()))
                .thenReturn(0);
        int expectedCount = 0;
        int actualCount = subscriptionService.getFollowingCount(3L);
        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void getFollowing() {
    }

    @Test
    void getFollowingCount() {
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(Mockito.anyLong()))
                .thenReturn(77);
        int expectedCount = 77;
        int actualCount = subscriptionService.getFollowingCount(3L);
        Assertions.assertEquals(expectedCount, actualCount);
        Mockito.verify(subscriptionRepository).findFolloweesAmountByFollowerId(3L);
    }

    @Test
    void getFollowingCountNegative() {
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(Mockito.anyLong()))
                .thenReturn(0);
        int expectedCount = 0;
        int actualCount = subscriptionService.getFollowingCount(3L);
        Assertions.assertEquals(expectedCount, actualCount);
        Mockito.verify(subscriptionRepository).findFolloweesAmountByFollowerId(3L);
    }
}
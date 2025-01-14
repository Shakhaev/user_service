package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.impl.SubscriptionServiceImpl;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;
    private long followerId;
    private long followeeId;

    @BeforeEach
    public void init() {
        followerId = 1L;
        followeeId = 2L;
    }

    @Test
    @DisplayName("Follow To Another User")
    void testFollowOneUserByAnotherUser() {
        subscriptionService.followUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Follow By Himself")
    void testFollowUserByHimself() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followerId))
                .thenThrow(new DataValidationException("!!"));
        Assert.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followerId));
    }

    @Test
    @DisplayName("Unfollow Another User")
    void testUnfollowOneUserFromAnotherUser() {
        subscriptionService.unfollowUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .unfollowUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Get Followers Count")
    void testGetFollowersCount() {
        subscriptionService.getFollowersCount(followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findFollowersAmountByFolloweeId(followeeId);
    }

    @Test
    @DisplayName("Get Following Count")
    void testGetFollowingCount() {
        subscriptionService.getFollowingCount(followerId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findFolloweesAmountByFollowerId(followerId);
    }

}

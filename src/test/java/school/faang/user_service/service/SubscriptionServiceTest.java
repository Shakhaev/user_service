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

import static reactor.core.publisher.Mono.when;
import static school.faang.user_service.exception.MessageError.USER_CANNOT_FOLLOW_TO_HIMSELF;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    SubscriptionRepository subscriptionRepository;
    @InjectMocks
    SubscriptionService subscriptionService;

    long followerId;
    long followeeId;

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
    @DisplayName("Follow To Himself")
    void testFollowUserByHimself() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followerId))
                .thenThrow(new DataValidationException(USER_CANNOT_FOLLOW_TO_HIMSELF));
        Assert.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followerId));
    }

    @Test
    @DisplayName("Unfollow Another User")
    void testUnfollowOneUserFromAnotherUser() {
        subscriptionRepository.unfollowUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .unfollowUser(followerId, followeeId);
    }

}

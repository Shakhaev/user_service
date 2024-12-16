package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.event.SubscriptionEvent;
import school.faang.user_service.publisher.SubscriptionEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.SubscriptionValidator;
import school.faang.user_service.validator.UserValidator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionValidator subscriptionValidator;

    @Mock
    private UserValidator userValidator;

    @Mock
    private SubscriptionEventPublisher subscriptionEventPublisher;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private final long followerId = 1;
    private final long followeeId = 2;

    @Test
    void followUser_shouldCallRepositoryAndPublisher() {
        doNothing().when(userValidator).validateUserById(followerId);
        doNothing().when(userValidator).validateUserById(followeeId);
        doNothing().when(subscriptionValidator).validateNoSelfSubscription(followerId, followeeId);
        doNothing().when(subscriptionValidator).validateSubscriptionCreation(followerId, followeeId);

        subscriptionService.followUser(followerId, followeeId);

        verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);
        verify(subscriptionEventPublisher, times(1)).publish(any(SubscriptionEvent.class));
    }

    @Test
    void unfollowUser_shouldCallValidatorAndRepository() {
        assertDoesNotThrow(() -> subscriptionService.unfollowUser(followerId, followeeId));

        verify(subscriptionValidator, times(1)).validateSubscriptionRemoval(followerId, followeeId);
        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    void getFollowersCount_shouldReturnCount() {
        long followeeId = 1;
        when(subscriptionRepository.findFollowersAmountByFolloweeId(followeeId)).thenReturn(5);
        long count = assertDoesNotThrow(() -> subscriptionService.getFollowersCount(followeeId));

        assertEquals(5, count);

        verify(userValidator, times(1)).validateUserById(followeeId);
    }

    @Test
    void getFollowingCount_shouldReturnCount() {
        when(subscriptionRepository.findFolloweesAmountByFollowerId(followerId)).thenReturn(3);
        long count = assertDoesNotThrow(() -> subscriptionService.getFollowingCount(followerId));

        assertEquals(3, count);

        verify(userValidator, times(1)).validateUserById(followerId);
    }
}
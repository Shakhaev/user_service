package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {

    @Mock
    SubscriptionRepository subscriptionRepository;
    @InjectMocks
    SubscriptionService subscriptionService;


    @Test
    void testFollowOneUserByAnother() {
        long userId1 = 1;
        long userId2 = 2;
        subscriptionService.followUser(userId1, userId2);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(userId1, userId2);
    }

    @Test
    void testFollowUserByHimself() {
        long userId1 = 1;

        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(userId1, userId1))
                .thenThrow(new DataValidationException(""));
        Assert.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(userId1, userId1));
    }
}

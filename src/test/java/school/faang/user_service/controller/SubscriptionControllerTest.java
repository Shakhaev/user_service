package school.faang.user_service.controller;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;


@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @Mock
    SubscriptionRepository subscriptionRepository;
    @Mock
    SubscriptionService subscriptionService;
    @InjectMocks
    SubscriptionController subscriptionController;

    @Test
    void testFollowUserToAnotherUser() {
        long userId1 = 1;
        long userId2 = 2;
        subscriptionController.followUser(userId1, userId2);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .followUser(userId1, userId2);
    }

    @Test
    void followUserByHimself() {
        long userId1 = 1;
        Assert.assertThrows(DataValidationException.class, () -> subscriptionController.followUser(userId1, userId1));
    }
}
package school.faang.user_service.controller;

import org.junit.jupiter.api.Assertions;
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
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void followUser() {
        SubscriptionController controller = new SubscriptionController(subscriptionService);
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(4, 5))
                .thenReturn(false);
        Assertions.assertDoesNotThrow(() -> controller.followUser(4, 5));
    }

    @Test
    void followSameUser() {
        SubscriptionController controller = new SubscriptionController(subscriptionService);
        Assertions.assertThrows(DataValidationException.class, () -> controller.followUser(4, 4),
                "FollowerId 4 and FolloweeId 4 cannot be the same");
    }

    @Test
    void followIsExist() {
        SubscriptionController controller = new SubscriptionController(subscriptionService);
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(4, 5))
                .thenReturn(true);
        Assertions.assertThrows(DataValidationException.class, () -> controller.followUser(4, 5),
                "This subscription (4 - 5) already exists");
    }
}
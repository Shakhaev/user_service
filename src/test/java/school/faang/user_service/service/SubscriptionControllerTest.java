package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SubscriptionController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Test
    void shouldCallServiceToFollowUserWhenIdsAreDifferent() {
        long followerId = 1L;
        long followeeId = 2L;

        subscriptionController.followUser(followerId, followeeId);

        Mockito.verify(subscriptionService, Mockito.times(1)).followUser(followerId, followeeId);
    }

    @Test
    void shouldThrowExceptionWhenFollowerIdEqualsFolloweeId() {
        long followerId = 1L;
        long followeeId = 1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                subscriptionController.followUser(followerId, followeeId)
        );

        assertEquals("You can't subscribe to yourself", exception.getMessage());
        Mockito.verify(subscriptionService, Mockito.never()).followUser(Mockito.anyLong(), Mockito.anyLong());
    }
}

package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SubscriptionControllerTest {
    @InjectMocks
    private SubscriptionController controller;

    @Mock
    private SubscriptionService service;

    @Test
    void testSubForAnotherUser() {
        long followerId = 1L;
        long followeeId = 2L;

        controller.followUser(followerId, followeeId);

        verify(service, times(1)).followUser(followerId, followeeId);
    }

    @Test
    void testSubForYourself() {
        long followerId = 1L;
        long followeeId = 1L;

        assertThrows(DataValidationException.class, () -> controller.followUser(followerId, followeeId));
    }
}
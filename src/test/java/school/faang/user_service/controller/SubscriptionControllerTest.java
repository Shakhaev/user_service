package school.faang.user_service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @MockBean
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionController controller;

    @Test
    void followUser() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(4, 5))
                .thenReturn(false);
        Assertions.assertDoesNotThrow(() -> controller.followUser(4, 5));
    }

    @Test
    void followSameUser() {
        Assertions.assertThrows(DataValidationException.class, () -> controller.followUser(4, 4),
                "FollowerId 4 and FolloweeId 4 cannot be the same");
    }

    @Test
    void followIsExist() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(4, 5))
                .thenReturn(true);
        Assertions.assertThrows(DataValidationException.class, () -> controller.followUser(4, 5),
                "This subscription (4 - 5) already exists");
    }

    @Test
    void unfollowUser() {
        Assertions.assertDoesNotThrow(() -> controller.unfollowUser(4, 5));
    }

    @Test
    void unfollowSameUser() {
        Assertions.assertThrows(DataValidationException.class, () -> controller.unfollowUser(4, 4),
                "FollowerId 4 and FolloweeId 4 cannot be the same");
    }

    @Test
    void getFollowersCount() {
        Mockito.when(subscriptionRepository.findFollowersAmountByFolloweeId(Mockito.anyLong()))
                .thenReturn(77);
        int expectedCount = 77;
        int actualCount = controller.getFollowersCount(3L);
        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void getFollowingCount() {
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(Mockito.anyLong()))
                .thenReturn(77);
        int expectedCount = 77;
        int actualCount = controller.getFollowingCount(3L);
        Assertions.assertEquals(expectedCount, actualCount);
    }

}
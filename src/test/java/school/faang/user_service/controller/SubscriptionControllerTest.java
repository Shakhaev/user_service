package school.faang.user_service.controller;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;


@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    //@Mock
    //SubscriptionRepository subscriptionRepository;
    @Mock
    private SubscriptionService subscriptionService;
    @InjectMocks
    private SubscriptionController subscriptionController;

    //UserMapper userMapper;
    private UserFilterDto userFilterDto;

    long followerId;
    long followeeId;

    @BeforeEach
    public void init() {
        followerId = 1L;
        followeeId = 2L;
    }

    @Test
    @DisplayName("Follow To Another User")
    void testFollowUserToAnotherUser() {
        subscriptionController.followUser(followerId, followeeId);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .followUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Follow To Himself")
    void testFollowUserByHimself() {
        Assert.assertThrows(DataValidationException.class, () -> subscriptionController.followUser(followerId, followerId));
    }

    @Test
    @DisplayName("Unfollow Another User")
    void testUnfollowUserFromAnotherUser() {
        subscriptionController.unfollowUser(followerId, followeeId);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .unfollowUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Unfollow User Himself")
    void testUnfollowUserByHimself() {
        Assert.assertThrows(DataValidationException.class, () -> subscriptionController.unfollowUser(followerId, followerId));
    }

    @Test
    @DisplayName("Get All Followers")
    void testGetAllFollowers() {
        userFilterDto = new UserFilterDto();
        subscriptionController.getFollowers(followerId, userFilterDto);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowers(followerId, userFilterDto);
    }
}
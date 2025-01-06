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
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;


@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService;
    @InjectMocks
    private SubscriptionController subscriptionController;

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
        Assert.assertThrows(DataValidationException.class,
                () -> subscriptionController.followUser(followerId, followerId));
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
        Assert.assertThrows(DataValidationException.class,
                () -> subscriptionController.unfollowUser(followerId, followerId));
    }

    @Test
    @DisplayName("Get All Followers")
    void testGetAllFollowers() {
        SubscriptionUserFilterDto subscriptionUserFilterDto = new SubscriptionUserFilterDto();
        subscriptionController.getFollowers(followerId, subscriptionUserFilterDto);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowers(followerId, subscriptionUserFilterDto);
    }

    @Test
    @DisplayName("Get Followers Count")
    void testGetFollowersCount() {
        subscriptionController.getFollowersCount(followeeId);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowersCount(followeeId);
    }

    @Test
    @DisplayName("Get Following Count")
    void testGetFollowingCount() {
        subscriptionController.getFollowingCount(followerId);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowingCount(followerId);
    }
}
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
import school.faang.user_service.service.impl.SubscriptionServiceImpl;


@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @Mock
    private SubscriptionServiceImpl subscriptionService;
    @InjectMocks
    private SubscriptionController subscriptionController;

    SubscriptionUserFilterDto subscriptionUserEmptyFilterDto;
    private long followerId;
    private long followeeId;

    @BeforeEach
    public void init() {
        followerId = 1L;
        followeeId = 2L;

        subscriptionUserEmptyFilterDto = SubscriptionUserFilterDto.builder().build();
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
        subscriptionController.getFollowers(followerId, subscriptionUserEmptyFilterDto);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowers(followerId, subscriptionUserEmptyFilterDto);
    }

    @Test
    @DisplayName("Get Followers Count")
    void testGetFollowersCount() {
        subscriptionController.getFollowersCount(followeeId);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowersCount(followeeId);
    }

    @Test
    @DisplayName("Get All Followees")
    void testGetAllFollowees() {
        subscriptionController.getFollowing(followeeId, subscriptionUserEmptyFilterDto);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowing(followeeId, subscriptionUserEmptyFilterDto);
    }

    @Test
    @DisplayName("Get Following Count")
    void testGetFollowingCount() {
        subscriptionController.getFollowingCount(followerId);
        Mockito.verify(subscriptionService, Mockito.times(1))
                .getFollowingCount(followerId);
    }
}
package school.faang.user_service.controller;

import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validator.SubscriptionValidator;

import java.util.List;

@Controller
public class SubscriptionController {

    private SubscriptionService subscriptionService;
    private SubscriptionValidator subscriptionValidator;
    private UserDto userDto;

    public void followUser(long followerId, long followeeId) {
        subscriptionValidator.validateFollowerAndFolloweeIds(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You can't unsubscribe from yourself");
        }

        subscriptionService.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    public int getFollowersCount(long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }
}

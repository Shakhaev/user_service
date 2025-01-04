package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

import static school.faang.user_service.exception.MessageError.USER_CANNOT_FOLLOW_TO_HIMSELF;
import static school.faang.user_service.exception.MessageError.USER_CANNOT_UNFOLLOW_FROM_HIMSELF;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException(USER_CANNOT_FOLLOW_TO_HIMSELF);
        }
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException(USER_CANNOT_UNFOLLOW_FROM_HIMSELF);
        }
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        return subscriptionService.getFollowing(followeeId, filter);
    }

    public int getFollowersCount(long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }


}

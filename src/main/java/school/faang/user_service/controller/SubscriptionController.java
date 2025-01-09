package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

import static school.faang.user_service.exception.MessageError.USER_CANNOT_FOLLOW_TO_HIMSELF;
import static school.faang.user_service.exception.MessageError.USER_CANNOT_UNFOLLOW_FROM_HIMSELF;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/follow")
    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException(USER_CANNOT_FOLLOW_TO_HIMSELF);
        }
        subscriptionService.followUser(followerId, followeeId);
    }

    @PostMapping("/unfollow")
    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException(USER_CANNOT_UNFOLLOW_FROM_HIMSELF);
        }
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("followers")
    public List<SubscriptionUserDto> getFollowers(long followeeId, SubscriptionUserFilterDto filter) {
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("following")
    public List<SubscriptionUserDto> getFollowing(long followeeId, SubscriptionUserFilterDto filter) {
        return subscriptionService.getFollowing(followeeId, filter);
    }

    @GetMapping("followers/count")
    public int getFollowersCount(long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    @GetMapping("following/count")
    public int getFollowingCount(long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }


}

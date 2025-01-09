package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.UserSubResponseDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/follow")
    public void followUser(@RequestParam long followerId, @RequestParam long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Follower and followee cannot be the same");
        } else if (followerId < 0 || followeeId < 0) {
            throw new DataValidationException("User IDs cannot be negative");
        } else {
            subscriptionService.followUser(followerId, followeeId);
        }
    }

    @PostMapping("/unfollow")
    public void unfollowUser(@RequestParam long followerId, @RequestParam long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Follower and followee cannot be the same");
        } else if (followerId < 0 || followeeId < 0) {
            throw new DataValidationException("User IDs cannot be negative");
        } else {
            subscriptionService.unfollowUser(followerId, followeeId);
        }
    }

    @GetMapping("/followers")
    public List<UserSubResponseDto> getFollowers(@RequestParam long followeeId, @RequestBody UserFilterDto filter) {
        if (followeeId < 0) {
            throw new DataValidationException("User ID cannot be negative");
        }
        return subscriptionService.getFollowers(followeeId, filter);
    }

    @GetMapping("/following/count")
    public int getFollowingCount(@RequestParam long followeeId) {
        if (followeeId < 0) {
            throw new DataValidationException("User ID cannot be negative");
        }
        return subscriptionService.getFollowingCount(followeeId);
    }

    @GetMapping("/following")
    public List<UserSubResponseDto> getFollowing(@RequestParam long followerId, @RequestBody UserFilterDto filter) {
        if (followerId < 0) {
            throw new DataValidationException("User ID cannot be negative");
        }
        return subscriptionService.getFollowing(followerId, filter);
    }

    @GetMapping("/followers/count")
    public int getFollowersCount(@RequestParam long followerId) {
        if (followerId < 0) {
            throw new DataValidationException("User ID cannot be negative");
        }
        return subscriptionService.getFollowersCount(followerId);
    }
}

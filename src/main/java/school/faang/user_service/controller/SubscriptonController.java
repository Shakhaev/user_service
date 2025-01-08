package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.FollowingFeatureDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptonController {
    private final SubscriptionService subscriptionService;

    @GetMapping("/followees/{followeeId}")
    public List<UserDto> getFollowees(@PathVariable long followeeId, @RequestBody UserFilterDto userFilterDto) {
        return subscriptionService.getFollowees(followeeId, userFilterDto);
    }

    @GetMapping("/followers")
    public List<UserDto> getFollowers(@PathVariable long followeeId, @RequestBody UserFilterDto userFilterDto) {
        return subscriptionService.getFollowers(followeeId, userFilterDto);
    }

    @GetMapping("/followeesCount")
    public long getFollowingCount(@RequestParam long followeeId) {
        return subscriptionService.getFollowingCount(followeeId);
    }

    @GetMapping("/followersCount")
    public long getFollowersCount(@RequestParam long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    @PostMapping("/follow")
    public void followerUser(@RequestBody FollowingFeatureDto followingFeatureDto) {
        subscriptionService.followUser(followingFeatureDto);
    }

    @DeleteMapping("/unfollow")
    public void unfollowUser(@RequestBody FollowingFeatureDto followingFeatureDto) {
        subscriptionService.unfollowUser(followingFeatureDto);
    }
}

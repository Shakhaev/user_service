package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.FollowingFeatureDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.interfaces.SubscriptionServiceI;

import java.util.List;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionServiceI subscriptionService;

    @GetMapping("/{followeeId}")
    public List<UserDto> getFollowees(@PathVariable long followeeId, @Valid @RequestBody UserFilterDto userFilterDto) {
        return subscriptionService.getFollowees(followeeId, userFilterDto);
    }

    @GetMapping("/followers/{followerId}")
    public List<UserDto> getFollowers(@PathVariable long followerId, @Valid @RequestBody UserFilterDto userFilterDto) {
        return subscriptionService.getFollowers(followerId, userFilterDto);
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
    public void followerUser(@Valid @RequestBody FollowingFeatureDto followingFeatureDto) {
        subscriptionService.followUser(followingFeatureDto);
    }

    @DeleteMapping("/unfollow")
    public void unfollowUser(@Valid @RequestBody FollowingFeatureDto followingFeatureDto) {
        subscriptionService.unfollowUser(followingFeatureDto);
    }
}

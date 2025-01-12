package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/subscription")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PatchMapping("following")
    private void follow(@RequestParam long followerId, @RequestParam long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    @DeleteMapping("following")
    private void unfollow(@RequestParam long followerId, @RequestParam long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("followers")
    private List<SubscriptionUserDto> getFollowers(
            @RequestParam long followeeId,
            @ModelAttribute UserFilterDto dto
    ) {
        return subscriptionService.getFollowers(followeeId, dto);
    }

    @GetMapping("followers/count")
    private int getFollowersCount(@RequestParam long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    @GetMapping("following")
    private List<SubscriptionUserDto> getFollowing(
            @RequestParam long followerId,
            @ModelAttribute UserFilterDto dto
    ) {
        return subscriptionService.getFollowing(followerId, dto);
    }

    @GetMapping("following/count")
    private int getFollowingCount(@RequestParam long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}

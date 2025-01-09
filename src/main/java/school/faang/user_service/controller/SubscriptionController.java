package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.subscription.SubscriptionFollowDto;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("follow")
    private void follow(@Valid @RequestBody SubscriptionFollowDto dto) {
        subscriptionService.followUser(dto.followerId(), dto.followeeId());
    }

    @DeleteMapping("follow")
    private void unfollow(@Valid @RequestBody SubscriptionFollowDto dto) {
        subscriptionService.unfollowUser(dto.followerId(), dto.followeeId());
    }

    @GetMapping("{followeeId}/followers")
    private List<SubscriptionUserDto> getFollowers(
            @PathVariable Long followeeId,
            @ModelAttribute() UserFilterDto dto
    ) {
        return subscriptionService.getFollowers(followeeId, dto);
    }

    @GetMapping("{followerId}/followers/count")
    private int getFollowersCount(@PathVariable Long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    @GetMapping("{followerId}/following")
    private List<SubscriptionUserDto> getFollowing(
            @PathVariable Long followerId,
            @ModelAttribute() UserFilterDto dto
    ) {
        return subscriptionService.getFollowing(followerId, dto);
    }

    @GetMapping("{followerId}/following/count")
    private int getFollowingCount(@PathVariable Long followerId) {
        return subscriptionService.getFollowingCount(followerId);
    }
}

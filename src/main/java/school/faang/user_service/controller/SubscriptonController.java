package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import school.faang.user_service.dto.FollowingFeatureDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
public class SubscriptonController {
    private final SubscriptionService subscriptionService;

    @GetMapping
    public CompletableFuture<List<UserDto>> getFollowers(long followeeId, UserFilterDto userFilterDto) {
        return subscriptionService.getFollowers(followeeId, userFilterDto);
    }

    @GetMapping
    public long getFollowersCount(long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    @PostMapping
    public void followerUser(FollowingFeatureDto followingFeatureDto) {
        subscriptionService.followUser(followingFeatureDto);
    }

    @DeleteMapping
    public void unfollowUser(FollowingFeatureDto followingFeatureDto) {
        subscriptionService.unfollowUser(followingFeatureDto);
    }
}

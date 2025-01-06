package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.FollowingFeatureDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class SubscriptonController {
    private final SubscriptionService subscriptionService;

    @GetMapping
    public CompletableFuture<List<UserDto>> getFollowees(long followeeId, UserFilterDto userFilterDto) {
        return subscriptionService.getFollowees(followeeId, userFilterDto);
    }

    @GetMapping
    public CompletableFuture<List<UserDto>> getFollowers(long followeeId, UserFilterDto userFilterDto) {
        return subscriptionService.getFollowers(followeeId, userFilterDto);
    }

    @GetMapping
    public long getFollowingCount(long followeeId) {
        return subscriptionService.getFollowingCount(followeeId);
    }

    @GetMapping
    public long getFollowersCount(long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
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

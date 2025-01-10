package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserMapper userMapper;

    @PostMapping("/follow/{followerId}/{followeeId}")
    public ResponseEntity<Void> followUser(@PathVariable long followerId, @PathVariable long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unfollow/{followerId}/{followeeId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable long followerId, @PathVariable long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/followers/{followeeId}")
    public ResponseEntity<List<SubscriptionUserDto>> getFollowers(
            @PathVariable long followeeId,
            @RequestBody(required = false) UserFilterDto filters) {

        List<SubscriptionUserDto> followers = subscriptionService
                .getFollowers(followeeId, filters).stream()
                .map(userMapper::toDto)
                .toList();

        return ResponseEntity.ok(followers);
    }

    @GetMapping("/followers/count/{followeeId}")
    public ResponseEntity<Integer> getFollowersCount(@PathVariable long followeeId) {
        int followersCount = subscriptionService.getFollowersCount(followeeId);
        return ResponseEntity.ok(followersCount);
    }

    @PostMapping("/following/{followerId}")
    public ResponseEntity<List<SubscriptionUserDto>> getFollowing(
            @PathVariable long followerId,
            @RequestBody(required = false) UserFilterDto filters) {

        List<SubscriptionUserDto> followings = subscriptionService
                .getFollowing(followerId, filters).stream()
                .map(userMapper::toDto)
                .toList();

        return ResponseEntity.ok(followings);
    }

    @GetMapping("/following/count/{followerId}")
    public ResponseEntity<Integer> getFollowingCount(@PathVariable long followerId) {
        int followingCount = subscriptionService.getFollowingCount(followerId);
        return ResponseEntity.ok(followingCount);
    }
}

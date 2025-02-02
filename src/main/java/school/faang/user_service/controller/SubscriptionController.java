package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.user.UserReadDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService service;

    public void followUser(long followerId, long followeeId) {
        service.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        service.unfollowUser(followerId, followeeId);
    }

    public List<UserReadDto> getFollowers(long followeeId, UserFilterDto filters) {
        return service.getFollowers(followeeId, filters);
    }

    public int getFollowersCount(long followeeId) {
        return service.getFollowersCount(followeeId);
    }

    public List<UserReadDto> getFollowing(long followeeId, UserFilterDto filters) {
        return service.getFollowing(followeeId, filters);
    }

    public int getFollowingCount(long followerId) {
        return service.getFollowingCount(followerId);
    }
}
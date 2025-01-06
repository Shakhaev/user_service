package school.faang.user_service.controller;

import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@Controller
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserMapper userMapper;

    public SubscriptionController(SubscriptionService subscriptionService, UserMapper userMapper) {
        this.subscriptionService = subscriptionService;
        this.userMapper = userMapper;
    }

    public void followUser(long followerId, long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followerId, UserFilterDto filter) {
        List<User> users = subscriptionService.getFollowers(followerId, filter);
        return userMapper.toDtoList(users);
    }

    public int getFollowersCount(long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        List<User> followingUsers = subscriptionService.getFollowing(followeeId, filter);
        return userMapper.toDtoList(followingUsers);
    }

    public int getFollowingCount(long followeeId) {
        return subscriptionService.getFollowingCount(followeeId);
    }
}

package school.faang.user_service.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.transfer.Details;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserMapper userMapper;

    public void followUser(long followerId, long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @JsonView(Details.class)
    public List<UserDto> getFollowers(long followerId, UserFilterDto filterDto) {
        List<User> users = subscriptionService.getFollowers(followerId, filterDto);
        return userMapper.toDtoList(users);
    }

    public int getFollowersCount(long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    @JsonView(Details.class)
    public List<UserDto> getFollowing(long followeeId, UserFilterDto filterDto) {
        List<User> followingUsers = subscriptionService.getFollowing(followeeId, filterDto);
        return userMapper.toDtoList(followingUsers);
    }

    public int getFollowingCount(long followeeId) {
        return subscriptionService.getFollowingCount(followeeId);
    }
}


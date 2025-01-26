package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.utility.validator.UserDtoValidator;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final UserMapper userMapper;
    private final UserDtoValidator userDtoValidator;

    public void followUser(long followerId, long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    @GetMapping("/getFollowers")
    public List<UserDto> getFollowers(@RequestParam long followerId, @RequestBody UserFilterDto filterDto) {
        List<User> users = subscriptionService.getFollowers(followerId, filterDto);
        List<UserDto> userDtos = userMapper.toDtoList(users);
        userDtoValidator.validate(userDtos);
        return userDtos;
    }

    public int getFollowersCount(long followerId) {
        return subscriptionService.getFollowersCount(followerId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filterDto) {
        List<User> users = subscriptionService.getFollowing(followeeId, filterDto);
        List<UserDto> userDtos = userMapper.toDtoList(users);
        userDtoValidator.validate(userDtos);
        return userDtos;
    }

    public int getFollowingCount(long followeeId) {
        return subscriptionService.getFollowingCount(followeeId);
    }
}


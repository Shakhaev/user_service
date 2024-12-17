package school.faang.user_service.service;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.user.UserDto;

import java.util.List;

public interface SubscriptionService {
    void followUser(long followerId, long followeeId);

    void unfollowUser(long followerId, long followeeId);

    List<UserDto> getFollowers(long followeeId, UserFilterDto filter);

    int getFollowingCount(long followerId);
}
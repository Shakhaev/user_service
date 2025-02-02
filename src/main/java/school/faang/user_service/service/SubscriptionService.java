package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import java.util.List;

public interface SubscriptionService {
    void followUser(long followerId, long followeeId);
    void unfollowUser(long followerId, long followeeId);
    List<UserDto> getFollowers(long followeeId, UserFilterDto filter);
    List<UserDto> getFollowing(long followeeId, UserFilterDto filter);
}

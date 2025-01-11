package school.faang.user_service.service;

import school.faang.user_service.dto.RecordsQuantityDto;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.dto.SubscriptionUserFilterDto;

import java.util.List;

public interface SubscriptionService {
    void followUser(long followerId, long followeeId);

    void unfollowUser(long followerId, long followeeId);

    List<SubscriptionUserDto> getFollowers(long followeeId, SubscriptionUserFilterDto filter);

    List<SubscriptionUserDto> getFollowing(long followeeId, SubscriptionUserFilterDto filter);

    RecordsQuantityDto getFollowersCount(long followeeId);

    RecordsQuantityDto getFollowingCount(long followerId);
}

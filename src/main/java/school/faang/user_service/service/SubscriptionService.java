package school.faang.user_service.service;

import school.faang.user_service.dto.RecordsQuantityDto;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.dto.SubscriptionUserFilterDto;

import java.util.List;

public interface SubscriptionService {
    public void followUser(long followerId, long followeeId);

    public void unfollowUser(long followerId, long followeeId);

    public List<SubscriptionUserDto> getFollowers(long followeeId, SubscriptionUserFilterDto filter);

    public List<SubscriptionUserDto> getFollowing(long followeeId, SubscriptionUserFilterDto filter);

    public RecordsQuantityDto getFollowersCount(long followeeId);

    public RecordsQuantityDto getFollowingCount(long followerId);
}

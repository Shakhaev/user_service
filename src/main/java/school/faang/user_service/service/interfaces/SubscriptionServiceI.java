package school.faang.user_service.service.interfaces;

import school.faang.user_service.dto.FollowingFeatureDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;

import java.util.List;

public interface SubscriptionServiceI {
    long getFollowersCount(long followeeId);
    long getFollowingCount(long followeeId);
    List<UserDto> getFollowees(long followeeId, UserFilterDto userFilterDto);
    List<UserDto> getFollowers(long followeeId, UserFilterDto userFilterDto);
    void followUser(FollowingFeatureDto followingFeatureDTO);
    void unfollowUser(FollowingFeatureDto followingFeatureDTO);
}

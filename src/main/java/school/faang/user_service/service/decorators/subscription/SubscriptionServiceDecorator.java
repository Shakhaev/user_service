package school.faang.user_service.service.decorators.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.dto.FollowingFeatureDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.service.interfaces.SubscriptionServiceI;

import java.util.List;

@Service
@RequiredArgsConstructor
public abstract class SubscriptionServiceDecorator implements SubscriptionServiceI {
    protected final SubscriptionService subscriptionService;

    @Override
    public long getFollowersCount(long followeeId) {
        return subscriptionService.getFollowersCount(followeeId);
    }

    @Override
    public long getFollowingCount(long followeeId) {
        return subscriptionService.getFollowingCount(followeeId);
    }

    @Override
    public List<UserDto> getFollowees(long followeeId, UserFilterDto userFilterDto) {
        return subscriptionService.getFollowees(followeeId, userFilterDto);
    }

    @Override
    public List<UserDto> getFollowers(long followeeId, UserFilterDto userFilterDto) {
        return subscriptionService.getFollowers(followeeId, userFilterDto);
    }

    @Override
    public void followUser(FollowingFeatureDto followingFeatureDTO) {
        subscriptionService.followUser(followingFeatureDTO);
    }

    @Override
    public void unfollowUser(FollowingFeatureDto followingFeatureDTO) {
        subscriptionService.unfollowUser(followingFeatureDTO);
    }
}

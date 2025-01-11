package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecordsQuantityDto;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SubscriptionUserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionFilter;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;
import java.util.stream.Stream;

import static school.faang.user_service.exception.MessageError.USER_ALREADY_HAS_THIS_FOLLOWER;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final List<SubscriptionFilter> subscriptionFilters;

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionUserMapper subscriptionUserMapper;

    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(USER_ALREADY_HAS_THIS_FOLLOWER);
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<SubscriptionUserDto> getFollowers(long followeeId, SubscriptionUserFilterDto filterDto) {
        return getFilteredUsers(followeeId, filterDto);
    }

    public List<SubscriptionUserDto> getFollowing(long followeeId, SubscriptionUserFilterDto filterDto) {
        return getFilteredUsers(followeeId, filterDto);
    }

    public RecordsQuantityDto getFollowersCount(long followeeId) {
        int recordsQuantity = subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
        return new RecordsQuantityDto(recordsQuantity);
    }

    public RecordsQuantityDto getFollowingCount(long followerId) {
        int recordsQuantity = subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
        return new RecordsQuantityDto(recordsQuantity);
    }

    private List<SubscriptionUserDto> getFilteredUsers(long userId, SubscriptionUserFilterDto filterDto) {
        Stream<User> userStream = subscriptionRepository.findByFolloweeId(userId);
        subscriptionFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.apply(userStream, filterDto));
        return subscriptionUserMapper.toDto(userStream.toList());
    }
}

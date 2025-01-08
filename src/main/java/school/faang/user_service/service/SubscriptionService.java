package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;

    @Transactional
    public void followUser(long followId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followId, followeeId)) {
            throw new DataValidationException("Пользователь уже подписан на данного пользователя");
        }
        subscriptionRepository.followUser(followId, followeeId);
    }

    @Transactional
    public void unfollow(long followId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followId, followeeId)) {
            throw new DataValidationException("Пользователь не подписан на данного пользователя");
        }
        subscriptionRepository.unfollowUser(followId, followeeId);
    }

    @Transactional
    public List<SubscriptionUserDto> getFollowers(Long followeeId, UserFilterDto dto) {
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        return applyFiltersAndPagination(dto, followers);
    }

    @Transactional
    public long getFollowersCount(Long followerId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followerId);
    }

    @Transactional
    public List<SubscriptionUserDto> getFollowing(Long followerId, UserFilterDto dto) {
        Stream<User> followers = subscriptionRepository.findByFollowerId(followerId);
        return applyFiltersAndPagination(dto, followers);
    }

    @Transactional
    public long getFollowingCount(Long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private List<SubscriptionUserDto> applyFiltersAndPagination(UserFilterDto dto, Stream<User> followers) {
        for (var userFilter : userFilters) {
            followers = userFilter.apply(followers, dto);
        }
        int pageSize = dto.getPageSize();
        int skip = (dto.getPage() - 1) * pageSize;
        return followers
                .skip(skip)
                .limit(pageSize)
                .map(userMapper::toDto)
                .toList();
    }
}

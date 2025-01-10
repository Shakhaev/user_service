package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.user.UserFilter;
import school.faang.user_service.mapper.SubscriptionMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final SubscriptionMapper subscriptionMapper;

    public void followUser(long followerId, long followeeId) {
        validateFollowerAndFollowee(followerId, followeeId);
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new BusinessException("Пользователь уже подписан на данного пользователя");
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        validateFollowerAndFollowee(followerId, followeeId);
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new BusinessException("Пользователь не подписан на данного пользователя");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<SubscriptionUserDto> getFollowers(long followeeId, UserFilterDto dto) {
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        return applyFiltersAndPagination(dto, followers);
    }

    public int getFollowersCount(Long followerId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followerId);
    }

    public List<SubscriptionUserDto> getFollowing(long followerId, UserFilterDto dto) {
        Stream<User> followers = subscriptionRepository.findByFollowerId(followerId);
        return applyFiltersAndPagination(dto, followers);
    }

    public int getFollowingCount(Long followerId) {
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
                .map(subscriptionMapper::toDto)
                .toList();
    }

    private void validateFollowerAndFollowee(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new BusinessException("Follower и followee не могут быть одинаковыми");
        }
        if (!userRepository.existsById(followerId)) {
            throw new DataValidationException("Пользователь с id " + followerId + " не найден");
        }
        if (!userRepository.existsById(followeeId)) {
            throw new DataValidationException("Пользователь с id " + followeeId + " не найден");
        }
    }
}

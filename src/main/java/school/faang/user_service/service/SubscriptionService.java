package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    @Transactional
    public void followUser(long followerId, long followeeId) throws DataValidationException {
        validateIds(followerId, followeeId);
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Эта подписка уже существует!");
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) throws DataValidationException {
        validateIds(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }
    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(long followeeId) {
        return subscriptionRepository.findByFolloweeId(followeeId)
                .map(userMapper::toDto)
                .toList();
    }

    private List<UserDto> filterUsers(Stream<User> users, UserFilterDto filters) {
        List<User> userList = users.toList();
        userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply((Stream<User>) userList, filters));
        return userList.stream().map(userMapper::toDto).toList();
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        try (Stream<User> users = subscriptionRepository.findByFollowerId(followerId)) {
            return filterUsers(users, filter);
        }
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private void validateIds(long followerId, long followeeId) throws DataValidationException {
        if (followerId == followeeId) {
            throw new DataValidationException("Нельзя подписаться или отписаться от самого себя");
        }
    }
}

package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.filters.user.UserFilter;
import school.faang.user_service.validator.SubscriptionValidator;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionValidator subscriptionValidator;
    private final List<UserFilter> userFilters;

    public void followUser(long followerId, long followeeId) {
        subscriptionValidator.validateNotSelfSubscription(
                "The user cannot subscribe to himself", followerId, followeeId);

        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("The user has already subscribed");
        }

        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionValidator.validateNotSelfSubscription(
                "The user cannot unsubscribe to himself", followerId, followeeId);

        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("The user has already been unsubscribed");
        }

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<User> getFollowers(long followeeId, UserFilterDto filters) {
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);
        filterUsers(users, filters);
        return users.toList();
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<User> getFollowing(long followerId, UserFilterDto filters) {
        Stream<User> users = subscriptionRepository.findByFollowerId(followerId);
        filterUsers(users, filters);
        return users.toList();
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private void filterUsers(Stream<User> users, UserFilterDto filters) {
        userFilters.stream()
                .filter(filter -> filter != null && filter.isApplicable(filters))
                .forEach(filter -> filter.apply(users, filters));
    }
}

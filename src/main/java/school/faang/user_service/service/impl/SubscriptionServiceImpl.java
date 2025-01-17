package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final List<SubscriptionFilter> subscriptionFilters;
    private final SubscriptionUserMapper subscriptionUserMapper;

    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.error("User id={} already follow to the user id={}", followerId, followeeId);
            throw new DataValidationException("User id=" + followerId + " already follow to the user id=" + followeeId);
        }
        subscriptionRepository.followUser(followerId, followeeId);
        log.info("User id={} follow to the user id={}", followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("User id={} unfollow the user id={}", followerId, followeeId);
    }

    public List<SubscriptionUserDto> getFollowers(long followeeId, SubscriptionUserFilterDto filterDto) {
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);
        return getFilteredUsers(users, filterDto);
    }

    public List<SubscriptionUserDto> getFollowing(long followeeId, SubscriptionUserFilterDto filterDto) {
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);
        return getFilteredUsers(users, filterDto);
    }

    public RecordsQuantityDto getFollowersCount(long followeeId) {
        int recordsQuantity = subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
        return new RecordsQuantityDto(recordsQuantity);
    }

    public RecordsQuantityDto getFollowingCount(long followerId) {
        int recordsQuantity = subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
        return new RecordsQuantityDto(recordsQuantity);
    }

    private List<SubscriptionUserDto> getFilteredUsers(Stream<User> users, SubscriptionUserFilterDto filterDto) {
        int applicableFilterNum = subscriptionFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto)).toList().size();
        SubscriptionUserPageFilter pageFilter = new SubscriptionUserPageFilter();
        Stream<User> filteredUsers;
        if (applicableFilterNum > 0) {
            filteredUsers = subscriptionFilters.stream()
                    .filter(filter -> filter.isApplicable(filterDto))
                    .peek(filter -> log.info("Applied filter {}", filter.getName()))
                    .flatMap(filter -> filter.apply(users, filterDto))
                    .peek(user -> log.info("Applied for user {}", user.getUsername()));
        } else {
            filteredUsers = users;
        }
        filteredUsers = pageFilter.apply(filteredUsers, filterDto);

        return filteredUsers
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();
    }
}

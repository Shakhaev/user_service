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

import static school.faang.user_service.exception.MessageError.USER_ALREADY_HAS_THIS_FOLLOWER;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final List<SubscriptionFilter> subscriptionFilters;
    private final SubscriptionUserMapper subscriptionUserMapper;

    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(USER_ALREADY_HAS_THIS_FOLLOWER);
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
        log.info("Followers of user id={} has got", followeeId);
        return getFilteredUsers(users, filterDto);
    }

    public List<SubscriptionUserDto> getFollowing(long followeeId, SubscriptionUserFilterDto filterDto) {
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);
        log.info("Get following of user id={}", followeeId);
        return getFilteredUsers(users, filterDto);
    }

    public RecordsQuantityDto getFollowersCount(long followeeId) {
        int recordsQuantity = subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
        log.info("Get count of followers of user id={}. Count={}", followeeId, recordsQuantity);
        return new RecordsQuantityDto(recordsQuantity);
    }

    public RecordsQuantityDto getFollowingCount(long followerId) {
        int recordsQuantity = subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
        log.info("Get count of following of user id={}. Count={}", followerId, recordsQuantity);
        return new RecordsQuantityDto(recordsQuantity);
    }

    private List<SubscriptionUserDto> getFilteredUsers(Stream<User> users, SubscriptionUserFilterDto filterDto) {
        log.info("Getting filtered list of users");
        return subscriptionFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .flatMap(filter -> filter.apply(users, filterDto))
                .map(subscriptionUserMapper::toSubscriptionUserDto)
                .toList();
    }
}

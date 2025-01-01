package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.FollowerEvent;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorMessages;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.followerevent.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final FollowerEventPublisher followerEventPublisher;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void followUser(long followerId, long followeeId) {
        log.info("SubscriptionServiceImpl -> followUser: followerId:{}  followeeId:{}", followerId, followeeId);

        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(ErrorMessages.M_FOLLOW_EXIST);
        }
        subscriptionRepository.followUser(followerId, followeeId);
        followerEventPublisher.publish(FollowerEvent.builder()
                .followerId(followerId)
                .followeeId(followeeId)
                .receivedAt(LocalDateTime.now())
                .build());
    }

    @Override
    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(ErrorMessages.M_FOLLOW_DOES_NOT_EXIST);
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Override
    @Transactional
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        Stream<User> userStream = subscriptionRepository
                .findByFolloweeId(followeeId)
                .filter(user -> userFilters.parallelStream()
                        .allMatch(userFilter -> userFilter.apply(user, filter)));

        if (filter.getPage() != null
                && filter.getPage() > 0
                && filter.getPageSize() != null
                && filter.getPageSize() > 0) {

            userStream = userStream.skip((long) filter.getPageSize() * (filter.getPage() - 1))
                    .limit(filter.getPageSize());
        }

        return userMapper.userListToUserDtoList(userStream.collect(Collectors.toList()));
    }

    @Override
    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
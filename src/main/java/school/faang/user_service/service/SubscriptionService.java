package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.FollowingFeatureDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.UserWasNotFoundException;
import school.faang.user_service.filters.interfaces.UserFilter;
import school.faang.user_service.mapper.UserFollowingMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserFollowingMapper userFollowingMapper;
    private final List<UserFilter> filters;
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

    public long getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public long getFollowingCount(long followeeId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followeeId);
    }

    @Transactional
    public List<UserDto> getFollowees(long followeeId, UserFilterDto userFilterDto) {
        Stream<User> followersOfUser = subscriptionRepository.findByFolloweeId(followeeId);
        return filterPeople(followersOfUser, userFilterDto);
    }

    @Transactional
    public List<UserDto> getFollowers(long followeeId, UserFilterDto userFilterDto) {
        Stream<User> followersOfUser = subscriptionRepository.findByFollowerId(followeeId);
        return filterPeople(followersOfUser, userFilterDto);
    }

    private List<UserDto> filterPeople(Stream<User> followersOfUser, UserFilterDto userFilterDto) {
        if (filters == null || filters.isEmpty()) {
            return filterOnlyLimitsSkips(followersOfUser, userFilterDto);
        }

        Stream<User> userStream = followersOfUser.parallel();
        for (UserFilter filter : filters) {
            if (filter.isAcceptable(userFilterDto)) {
                userStream = filter.accept(userStream, userFilterDto);
            }
        }
        return filterOnlyLimitsSkips(followersOfUser, userFilterDto);
    }

    public void followUser(FollowingFeatureDto followingFeatureDTO) {
        long followerId = followingFeatureDTO.followerId();
        long followeeId = followingFeatureDTO.followeeId();

        logger.info("Trying to follow to user! : {} -> {}", followerId, followeeId);
        checkIfOneUser(followerId, followeeId);

        User requestUser = findUserById(followerId);
        User requestedUser = findUserById(followeeId);
        List<User> followedUsers = requestUser.getFollowees();
        List<User> followingUsers = requestedUser.getFollowers();

        if (existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            logger.error("The user already followed! : {} -> {}", followerId, followeeId);
        }

        followedUsers.add(requestedUser);
        followingUsers.add(requestUser);

        userRepository.save(requestUser);
        userRepository.save(requestedUser);

        logger.info("Succeed of following user!");
    }

    public void unfollowUser(FollowingFeatureDto followingFeatureDTO) {
        long followerId = followingFeatureDTO.followerId();
        long followeeId = followingFeatureDTO.followeeId();

        logger.info("Trying to unfollow to user! : {} -> {}", followerId, followeeId);
        checkIfOneUser(followerId, followeeId);
        isFollowingUserNotFollower(followerId, followeeId);

        User requestUser = findUserById(followerId);
        User requestedUser = findUserById(followeeId);
        List<User> followedUsers = requestUser.getFollowees();
        List<User> followingUsers = requestedUser.getFollowers();

        followedUsers.remove(requestedUser);
        followingUsers.remove(requestUser);

        userRepository.save(requestedUser);
        userRepository.save(requestedUser);

        logger.info("Succeed of unfollowing user!");
    }

    private boolean isFollowingUserNotFollower(long followerId, long followeeId) {
        if (!findUserById(followerId).getFollowees().contains(findUserById(followeeId))) {
            logger.error("The followerId is not following followeeId : {} -> {}", followerId, followeeId);
            throw new DataValidationException("Trying to follow to person not followed!");
        }
        return true;
    }

    private boolean existsByFollowerIdAndFolloweeId(long followerId, long followeeId) {
        User requestUser = findUserById(followerId);
        User requestedUser = findUserById(followeeId);

        logger.info("Checked existing by follower and followee!");
        return requestUser.getFollowees().contains(requestedUser);
    }

    private User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserWasNotFoundException("User was not found with id : " + id));
    }

    private void checkIfOneUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            logger.error("The followerId & followeeId is equals : {} -> {}", followerId, followeeId);
            throw new DataValidationException("Trying to follow to yourself!");
        }
    }

    private List<UserDto> filterOnlyLimitsSkips(Stream<User> followers, UserFilterDto userFilterDto) {
        return followers
                .skip((long) userFilterDto.page() * userFilterDto.pageSize())
                .limit(userFilterDto.pageSize())
                .map(userFollowingMapper::toDto)
                .toList();
    }
}

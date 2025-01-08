package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.FollowingFeatureDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.UserWasNotFoundException;
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
        return followersOfUser
                .parallel()
                .filter(user -> matchesPattern(userFilterDto.namePattern(), user.getUsername()))
                .filter(user -> matchesPattern(userFilterDto.aboutPattern(), user.getAboutMe()))
                .filter(user -> matchesPattern(userFilterDto.emailPattern(), user.getEmail()))
                .filter(user -> userFilterDto.contactPattern() == null ||
                        user.getContacts().stream()
                                .allMatch(contact -> matchesPattern(userFilterDto.contactPattern(), contact.getContact())))
                .filter(user -> matchesPattern(userFilterDto.countryPattern(), user.getCountry().getTitle()))
                .filter(user -> matchesPattern(userFilterDto.cityPattern(), user.getCity()))
                .filter(user -> matchesPattern(userFilterDto.phonePattern(), user.getPhone()))
                .filter(user -> userFilterDto.skillPattern() == null ||
                        user.getSkills().stream()
                                .allMatch(skill -> matchesPattern(userFilterDto.skillPattern(), skill.getTitle())))
                .filter(user -> userFilterDto.experienceMin() <= user.getExperience() &&
                        userFilterDto.experienceMax() >= user.getExperience())
                .skip((long) userFilterDto.page() * userFilterDto.pageSize())
                .limit(userFilterDto.pageSize())
                .map(userFollowingMapper::toDto)
                .toList();
    }

    public ResponseEntity<Void> followUser(FollowingFeatureDto followingFeatureDTO) {
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
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        followedUsers.add(requestedUser);
        followingUsers.add(requestUser);

        userRepository.save(requestUser);
        userRepository.save(requestedUser);

        logger.info("Succeed of following user!");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> unfollowUser(FollowingFeatureDto followingFeatureDTO) {
        long followerId = followingFeatureDTO.followerId();
        long followeeId = followingFeatureDTO.followeeId();

        logger.info("Trying to unfollow to user! : {} -> {}", followerId, followeeId);
        checkIfOneUser(followerId, followeeId);
        ifFollowingUserNotFollower(followerId, followeeId);

        User requestUser = findUserById(followerId);
        User requestedUser = findUserById(followeeId);
        List<User> followedUsers = requestUser.getFollowees();
        List<User> followingUsers = requestedUser.getFollowers();

        followedUsers.remove(requestedUser);
        followingUsers.remove(requestUser);

        userRepository.save(requestedUser);
        userRepository.save(requestedUser);

        logger.info("Succeed of unfollowing user!");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public boolean ifFollowingUserNotFollower(long followerId, long followeeId) {
        if (!findUserById(followerId).getFollowees().contains(findUserById(followeeId))) {
            logger.error("The followerId is not following followeeId : {} -> {}", followerId, followeeId);
            throw new DataValidationException("Trying to follow to person not followed!");
        }
        return true;
    }

    public boolean existsByFollowerIdAndFolloweeId(long followerId, long followeeId) {
        User requestUser = findUserById(followerId);
        User requestedUser = findUserById(followeeId);
        List<User> followedUsers = requestUser.getFollowers();

        logger.info("Checked existing by follower and followee!");
        return followedUsers.stream()
                .anyMatch(user -> user.getFollowees().contains(requestedUser));
    }

    public User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserWasNotFoundException("User was not found with id : " + id));
    }

    public void checkIfOneUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            logger.error("The followerId & followeeId is equals : {} -> {}", followerId, followeeId);
            throw new DataValidationException("Trying to follow to yourself!");
        }
    }

    public boolean matchesPattern(String pattern, String value) {
        return pattern == null || value.matches(pattern);
    }
}

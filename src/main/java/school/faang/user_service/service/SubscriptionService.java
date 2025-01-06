package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.FollowingFeatureDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.UserWasNotFoundException;
import school.faang.user_service.mapper.UserFollowingMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserFollowingMapper userFollowingMapper;
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

    /*
        TODO -> Needs to test
    */
    public long getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    /*
        TODO -> Needs to test
    */
    public long getFollowingCount(long followeeId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followeeId);
    }

     /*
        TODO -> Needs to test
    */
    public CompletableFuture<List<UserDto>> getFollowees(long followeeId, UserFilterDto userFilterDto) {
        Stream<User> followersOfUser = subscriptionRepository.findByFolloweeId(followeeId);
        return filterPeople(followersOfUser, userFilterDto);
    }

    /*
        TODO -> Needs to test
    */
    public CompletableFuture<List<UserDto>> getFollowers(long followeeId, UserFilterDto userFilterDto) {
        Stream<User> followersOfUser = subscriptionRepository.findByFollowerId(followeeId);
        return filterPeople(followersOfUser, userFilterDto);
    }

    private CompletableFuture<List<UserDto>> filterPeople(Stream<User> followersOfUser, UserFilterDto userFilterDto) {
        List<User> followers = followersOfUser.toList();

        logger.info("Halfing the list of followers async go!");
        int middle = followers.size() / 2;
        List<User> firstHalf = followers.subList(0, middle);
        List<User> secondHalf = followers.subList(middle, followers.size());

        CompletableFuture<List<UserDto>> firstTask = processPeople(firstHalf.stream(), userFilterDto);
        CompletableFuture<List<UserDto>> secondTask = processPeople(secondHalf.stream(), userFilterDto);

        return firstTask.thenCombine(secondTask, (firstHalfResult, secondHalfResult) -> {
            List<UserDto> result = new ArrayList<>();
            result.addAll(firstHalfResult);
            result.addAll(secondHalfResult);
            logger.info("Returning the combining!!");
            return result;
        });
    }

    @Async
    private CompletableFuture<List<UserDto>> processPeople(Stream<User> followersStream, UserFilterDto userFilterDto) {
        logger.info("Making some filters -> {}!", followersStream.toList());

        return CompletableFuture.completedFuture(followersStream
                .filter(user -> matchesPattern(userFilterDto.getNamePattern(), user.getUsername()))
                .filter(user -> matchesPattern(userFilterDto.getAboutPattern(), user.getAboutMe()))
                .filter(user -> matchesPattern(userFilterDto.getEmailPattern(), user.getEmail()))
                .filter(user -> userFilterDto.getContactPattern() == null ||
                        user.getContacts().stream()
                                .allMatch(contact -> matchesPattern(userFilterDto.getContactPattern(), contact.getContact())))
                .filter(user -> matchesPattern(userFilterDto.getCountryPattern(), user.getCountry().getTitle()))
                .filter(user -> matchesPattern(userFilterDto.getCityPattern(), user.getCity()))
                .filter(user -> matchesPattern(userFilterDto.getPhonePattern(), user.getPhone()))
                .filter(user -> userFilterDto.getSkillPattern() == null ||
                        user.getSkills().stream()
                                .allMatch(skill -> matchesPattern(userFilterDto.getSkillPattern(), skill.getTitle())))
                .filter(user -> userFilterDto.getExperienceMin() <= user.getExperience() &&
                        userFilterDto.getExperienceMax() >= user.getExperience())
                .skip((long) userFilterDto.getPage() * userFilterDto.getPageSize())
                .limit(userFilterDto.getPageSize())
                .map(userFollowingMapper::toDto)
                .toList());
    }

    /*
        TODO -> Needs to test
    */
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

    /*
        TODO -> Needs to test
     */
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

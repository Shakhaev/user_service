package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.UserWasNotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

    public ResponseEntity<Void> followUser(long followerId, long followeeId) {
        logger.info("Trying to follow to user! : {} -> {}", followerId, followeeId);
        if (followerId == followeeId) {
            logger.error("The followerId & followeeId is equals : {} -> {}", followerId, followeeId);
            throw new DataValidationException("Trying to follow to yourself!");
        }

        User requestUser = findUserById(followerId);
        User requestedUser = findUserById(followeeId);
        List<User> followedUsers = requestUser.getFollowees();
        List<User> followingUser = requestedUser.getFollowers();

        if (existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        followedUsers.add(requestedUser);
        followingUser.add(requestUser);

        userRepository.save(requestUser);
        userRepository.save(requestedUser);

        logger.info("Succeed of following user!");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public boolean existsByFollowerIdAndFolloweeId(long followerId, long followeeId) {
        User user = userRepository.findById(followerId)
                .orElseThrow(() -> new UserWasNotFoundException("User was not found with id : " + followerId));
        List<User> followedUsers = user.getFollowers();

        logger.info("Checked existing by follower and followee!");
        return followedUsers.stream()
                .anyMatch(followedUsers::contains);
    }

    public User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserWasNotFoundException("User was not found with id : " + id));
    }
}

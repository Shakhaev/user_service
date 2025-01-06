package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserFilter;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException(
                    "FollowerId %d and FolloweeId %d cannot be the same".formatted(followerId, followeeId)
            );
        }
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(
                    "This subscription (%d - %d) already exists".formatted(followerId, followeeId)
            );
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException(
                    "FollowerId %d and FolloweeId %d cannot be the same".formatted(followerId, followeeId)
            );
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<User> getFollowers(long followerId, UserFilter filter) {
        Stream<User> getAllFollowers = subscriptionRepository.findByFollowerId(followerId);
        return filterUsers(getAllFollowers, filter);
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<User> getFollowing(long followeeId, UserFilter filter) {
        Stream<User> followingStream = subscriptionRepository.findByFolloweeId(followeeId);
        return filterUsers(followingStream, filter);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private List<User> filterUsers(Stream<User> users, UserFilter filter) {
        return users.filter(user -> Pattern.matches(filter.getNamePattern(), user.getUsername()))
                .filter(user -> Pattern.matches(filter.getAboutPattern(), user.getAboutMe()))
                .filter(user -> Pattern.matches(filter.getEmailPattern(), user.getEmail()))
                .filter(user -> user.getContacts().stream()
                        .anyMatch(contact -> Pattern.matches(filter.getContactPattern(), contact.getContact())))
                .filter(user -> Pattern.matches(filter.getCountryPattern(), user.getCountry().getTitle()))
                .filter(user -> Pattern.matches(filter.getCityPattern(), user.getCity()))
                .filter(user -> Pattern.matches(filter.getPhonePattern(), user.getPhone()))
                .filter(user -> user.getSkills().stream()
                        .anyMatch(skill -> Pattern.matches(filter.getSkillPattern(), skill.getTitle())))
                .filter(user -> user.getExperience() >= filter.getExperienceMin())
                .filter(user -> user.getExperience() <= filter.getExperienceMax())
                .sorted(Comparator.comparing(User::getId))
                .limit((long) filter.getPageSize() * filter.getPage())
                .toList();
    }
}

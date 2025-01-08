package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SubscriptionUserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static school.faang.user_service.exception.MessageError.USER_ALREADY_HAS_THIS_FOLLOWER;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionUserMapper subscriptionUserMapper;
    private SubscriptionUserFilterDto subscriptionUserFilterDto;

    public void followUser(long followerId, long followeeId) {

        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(USER_ALREADY_HAS_THIS_FOLLOWER);
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<SubscriptionUserDto> getFollowers(long followeeId, SubscriptionUserFilterDto filter) {
        this.subscriptionUserFilterDto = filter;
        int pageNum = 0;
        int pageSize = 0;

        if (this.subscriptionUserFilterDto.getPageSize() > 0) {
            pageSize = this.subscriptionUserFilterDto.getPageSize();
        }

        if (this.subscriptionUserFilterDto.getPage() > 0) {
            pageNum = this.subscriptionUserFilterDto.getPage();
        }

        Stream<User> userStream = subscriptionRepository.findByFollowerId(followeeId);

        return userStream
                .filter(this::filterUsers)
                .map(subscriptionUserMapper::toDto)
                .skip((long) (pageNum - 1) * pageSize)
                .limit(pageSize)
                .toList();
    }

    public List<SubscriptionUserDto> getFollowing(long followeeId, SubscriptionUserFilterDto filter) {
        this.subscriptionUserFilterDto = filter;
        int pageNum = 0;

        int pageSize = 0;
        if (this.subscriptionUserFilterDto.getPageSize() > 0) {
            pageSize = this.subscriptionUserFilterDto.getPageSize();
        }

        if (this.subscriptionUserFilterDto.getPage() > 0) {
            pageNum = this.subscriptionUserFilterDto.getPage();
        }

        Stream<User> userStream = subscriptionRepository.findByFolloweeId(followeeId);

        return userStream
                .filter(this::filterUsers)
                .map(subscriptionUserMapper::toDto)
                .skip((long) (pageNum - 1) * pageSize)
                .limit(pageSize)
                .toList();

    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private boolean filterUsers(User user) {
        Predicate<User> filterUserName = checkStringField(User::getUsername,
                subscriptionUserFilterDto.getNamePattern());
        Predicate<User> filterAboutUser = checkStringField(User::getAboutMe,
                subscriptionUserFilterDto.getAboutPattern());
        Predicate<User> filterUserEmail = checkStringField(User::getEmail,
                subscriptionUserFilterDto.getEmailPattern());
        Predicate<User> filterUserCity = checkStringField(User::getCity,
                subscriptionUserFilterDto.getCityPattern());
        Predicate<User> filterUserPhone = checkStringField(User::getPhone,
                subscriptionUserFilterDto.getPhonePattern());
        Predicate<User> filterUserCountry = checkStringField(user1 -> user1.getCountry().getTitle(),
                subscriptionUserFilterDto.getCountryPattern());
        Predicate<User> filterUserSkills = checkSkills(
                subscriptionUserFilterDto.getSkillPattern());
        Predicate<User> filterUserContacts = checkContacts(
                subscriptionUserFilterDto.getContactPattern());
        Predicate<User> filterUserExperience = checkExperience(
                subscriptionUserFilterDto.getExperienceMin(), subscriptionUserFilterDto.getExperienceMax());

        return filterUserName
                .and(filterAboutUser)
                .and(filterUserEmail)
                .and(filterUserContacts)
                .and(filterUserCountry)
                .and(filterUserCity)
                .and(filterUserPhone)
                .and(filterUserSkills)
                .and(filterUserExperience)
                .test(user);
    }

    private Predicate<User> checkStringField(Function<User, String> fieldName, String checkPattern) {
        Predicate<User> checkPredicate;
        if (checkPattern != null && !checkPattern.isEmpty()) {
            checkPredicate = user -> fieldName.apply(user).matches(checkPattern);
        } else {
            checkPredicate = user -> true;
        }
        return checkPredicate;
    }

    private Predicate<User> checkSkills(String checkPattern) {
        Predicate<User> checkPredicate;
        if (checkPattern != null && !checkPattern.isEmpty()) {
            checkPredicate = user -> user.getSkills().stream().anyMatch(s -> s.getTitle().matches(checkPattern));
        } else {
            checkPredicate = user -> true;
        }
        return checkPredicate;
    }

    private Predicate<User> checkContacts(String checkPattern) {
        Predicate<User> checkPredicate;
        if (checkPattern != null && !checkPattern.isEmpty()) {
            checkPredicate = user -> user.getContacts().stream().anyMatch(c -> c.getContact().matches(checkPattern));
        } else {
            checkPredicate = user -> true;
        }
        return checkPredicate;
    }

    private Predicate<User> checkExperience(int minExperience, int maxExperience) {
        Predicate<User> checkPredicate;
        if (minExperience > 0 && maxExperience > 0 && maxExperience > minExperience) {
            checkPredicate = user -> (user.getExperience() >= minExperience && user.getExperience() < maxExperience);
        } else {
            checkPredicate = user -> true;
        }
        return checkPredicate;
    }


}

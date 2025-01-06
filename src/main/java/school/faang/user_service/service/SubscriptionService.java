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
        Stream<User> userStream = subscriptionRepository.findByFollowerId(followeeId);

        return userStream.filter(this::filterUsers).map(subscriptionUserMapper::toDto).toList();
    }

    public List<SubscriptionUserDto> getFollowing(long followeeId, SubscriptionUserFilterDto filter) {
        this.subscriptionUserFilterDto = filter;
        Stream<User> userStream = subscriptionRepository.findByFolloweeId(followeeId);

        return userStream
                .filter(this::filterUsers)
                .map(subscriptionUserMapper::toDto)
                .toList();

    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private boolean filterUsers(User user) {
        /*Predicate<User> checkUserName;
        String UsernamePattern = subscriptionUserFilterDto.getNamePattern();
        if (UsernamePattern != null && !UsernamePattern.isEmpty()) {
            checkUserName = u -> u.getUsername().matches(subscriptionUserFilterDto.getNamePattern());
        } else {
            checkUserName = u -> true;
        }*/

        Predicate<User> filterUserName = checkPattern(User::getUsername,
                subscriptionUserFilterDto.getNamePattern());
        Predicate<User> filterAboutUser = checkPattern(User::getAboutMe,
                subscriptionUserFilterDto.getAboutPattern());
        Predicate<User> filterUserEmail = checkPattern(User::getEmail,
                subscriptionUserFilterDto.getEmailPattern());
        Predicate<User> filterUserCity = checkPattern(User::getCity,
                subscriptionUserFilterDto.getCityPattern());
        Predicate<User> filterUserPhone = checkPattern(User::getPhone,
                subscriptionUserFilterDto.getPhonePattern());
        Predicate<User> filterUserCountry = checkPattern(user1 -> user1.getCountry().getTitle(),
                subscriptionUserFilterDto.getCountryPattern());
        //Predicate<User> filterUserSkills = checkSkillsPattern(
        //        subscriptionUserFilterDto.getSkillPattern());


        return filterUserName
                .and(filterAboutUser)
                .and(filterUserEmail)
                //.and(checkContact)
                .and(filterUserCountry)
                .and(filterUserCity)
                .and(filterUserPhone)
                //.and(filterUserSkills)
                .test(user);
    }

    private Predicate<User> checkPattern(Function<User, String> fieldName, String checkPattern) {
        Predicate<User> checkPredicate;
        if (checkPattern != null && !checkPattern.isEmpty()) {
            checkPredicate = user -> fieldName.apply(user).matches(checkPattern);
        } else {
            checkPredicate = u -> true;
        }
        return checkPredicate;
    }

/*    private Predicate<User> checkContactPattern(List<Contact> contacts, String checkPattern) {
        Predicate<User> checkPredicate;
        if (checkPattern != null && !checkPattern.isEmpty()) {
            checkPredicate = u -> contacts.stream()
                    .filter(contact -> contact.getContact().matches(checkPattern))
                    .isParallel();
        } else {
            checkPredicate = u -> true;
        }
        return checkPredicate;
    }
    private Predicate<User> checkSkillsPattern(String checkPattern) {
        Predicate<User> checkPredicate;
        if (checkPattern != null && !checkPattern.isEmpty()) {
            checkPredicate = u -> u.getSkills().contains(checkPattern);

        } else {
            checkPredicate = u -> true;
        }
        return checkPredicate;
    }
*/
}

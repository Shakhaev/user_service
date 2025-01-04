package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static school.faang.user_service.exception.MessageError.USER_ALREADY_HAS_THIS_FOLLOWER;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private UserFilterDto userFilterDto;

    public void followUser(long followerId, long followeeId) {

        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(USER_ALREADY_HAS_THIS_FOLLOWER);
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        this.userFilterDto = filter;
        Stream<User> userStream = subscriptionRepository.findByFollowerId(followeeId);

        return userStream.filter(this::filterUsers).map(userMapper::toDto).toList();
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        this.userFilterDto = filter;
        Stream<User> userStream = subscriptionRepository.findByFolloweeId(followeeId);

        return userStream.filter(this::filterUsers).map(userMapper::toDto).toList();

    }



    private boolean filterUsers(User user) {

        Predicate<User> checkUserName = u -> u.getUsername().matches(userFilterDto.getNamePattern());
        Predicate<User> checkAboutUser = u -> u.getAboutMe().matches(userFilterDto.getAboutPattern());
        Predicate<User> checkUserEmail = u -> u.getEmail().matches(userFilterDto.getEmailPattern());
        Predicate<User> checkContact = u -> u.getContacts().stream().map(Contact::getContact)
                .filter(c -> c.matches(userFilterDto.getContactPattern())).isParallel();
        Predicate<User> checkUserCountry = u -> u.getCountry().getTitle().matches(userFilterDto.getCountryPattern());
        Predicate<User> checkUserCity = u -> u.getCity().matches(userFilterDto.getCityPattern());
        Predicate<User> checkUserPhone = u -> u.getPhone().matches(userFilterDto.getPhonePattern());
        Predicate<User> checkUserSkill = u -> u.getSkills().stream().map(Skill::getTitle)
                .filter(title -> title.matches(userFilterDto.getSkillPattern())).isParallel();

        return checkUserName
                .and(checkAboutUser)
                .and(checkUserEmail)
                .and(checkContact)
                .and(checkUserCountry)
                .and(checkUserCity)
                .and(checkUserPhone)
                .and(checkUserSkill)
                .test(user);
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}

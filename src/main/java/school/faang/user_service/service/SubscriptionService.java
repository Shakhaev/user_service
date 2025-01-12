package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new IllegalArgumentException("This subscriber already exists");
        }

        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new IllegalArgumentException("You are not subscribed to this user");
        }

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);

        return filterUsers(followers, filter).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    private List<User> filterUsers(Stream<User> followers, UserFilterDto filter) {
        return followers
                .filter(user -> filter.getNamePattern() == null || user.getUsername().contains(filter.getNamePattern()))
                .filter(user -> filter.getEmailPattern() == null || user.getEmail().contains(filter.getEmailPattern()))
                .filter(user -> filter.getCountryPattern() == null || user.getCountry().getTitle().contains(filter.getCountryPattern()))
                .filter(user -> filter.getAboutPattern() == null || user.getAboutMe().contains(filter.getAboutPattern()))
                .filter(user -> filter.getContactPattern() == null
                        || user.getContacts().stream()
                        .anyMatch(contact -> contact.getContact().contains(filter.getContactPattern())))
                .filter(user -> filter.getCityPattern() == null || user.getCity().contains(filter.getCityPattern()))
                .filter(user -> filter.getPhonePattern() == null || user.getPhone().contains(filter.getPhonePattern()))
                .filter(user -> filter.getSkillPattern() == null
                        || user.getSkills().stream().anyMatch(skill -> skill.getTitle().contains(filter.getSkillPattern())))
                .filter(user -> filter.getExperienceMin() <= user.getExperience())
                .filter(user -> filter.getExperienceMax() >= user.getExperience())
                .skip((long) filter.getPage() * filter.getPageSize())
                .limit(filter.getPageSize())
                .collect(Collectors.toList());
    }
}

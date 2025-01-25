package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository repository;

    public void followUser(long followerId, long followeeId) {
        checkingActionsOnYourself(followerId, followeeId, "Нельзя подписаться на свой аккаунт.");

        boolean isThereSub = repository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (isThereSub) {
            throw new DataValidationException("Вы уже подписаны на этого пользователя.");
        }

        repository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        checkingActionsOnYourself(followerId, followeeId, "Нельзя отписаться от самого себя.");

        boolean isThereSub = repository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (!isThereSub) {
            throw new DataValidationException("Невозможно отписаться от пользователя, на которого вы не подписаны.");
        }

        repository.unfollowUser(followerId, followeeId);
    }

    private void checkingActionsOnYourself(long followerId, long followeeId, String errorMessage) {
        if (followerId == followeeId) {
            throw new DataValidationException(errorMessage);
        }
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        Stream<User> followers = repository.findByFolloweeId(followeeId);

        return filterUsers(followers, filter)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private Stream<User> filterUsers(Stream<User> users, UserFilterDto filter) {
        return users.filter(user -> {
            boolean matches = true;

            if (filter.getNamePattern() != null) {
                matches &= user.getUsername() != null && user.getUsername().contains(filter.getNamePattern());
            }

            if (filter.getAboutPattern() != null) {
                matches &= user.getAboutMe() != null && user.getAboutMe().contains(filter.getAboutPattern());
            }

            if (filter.getEmailPattern() != null) {
                matches &= user.getEmail() != null && user.getEmail().contains(filter.getEmailPattern());
            }

            if (filter.getContactPattern() != null) {
                matches &= user.getPhone() != null && user.getPhone().contains(filter.getContactPattern());
            }

            if (filter.getCountryPattern() != null) {
                matches &= user.getCountry() != null
                        && user.getCountry().getTitle() != null
                        && user.getCountry().getTitle().contains(filter.getCountryPattern());
            }

            if (filter.getCityPattern() != null) {
                matches &= user.getCity() != null && user.getCity().contains(filter.getCityPattern());
            }

            if (filter.getSkillPattern() != null) {
                matches &= user.getSkills() != null && user.getSkills().stream()
                        .anyMatch(skill -> skill.getTitle() != null
                                && skill.getTitle().contains(filter.getSkillPattern()));
            }

            if (filter.getExperienceMin() > 0) {
                matches &= user.getExperience() != null && user.getExperience() >= filter.getExperienceMin();
            }

            if (filter.getExperienceMax() > 0) {
                matches &= user.getExperience() != null && user.getExperience() <= filter.getExperienceMax();
            }

            return matches;
        });
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public int getFollowersCount(long followeeId) {
        return repository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        Stream<User> following = repository.findByFolloweeId(followeeId);

        return filterUsers(following, filter)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
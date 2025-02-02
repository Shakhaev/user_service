package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exeption.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final SubscriptionRepository subscriptionRepository;

    public void followUser(long followerId, long followeeId) {
        if (followerId > 0 && followeeId > 0) {
            subscriptionService.followUser(followerId, followeeId);
        } else {
            throw new DataValidationException("Некорректные ID.");
        }
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId > 0 && followeeId > 0) {
            subscriptionService.unfollowUser(followerId, followeeId);
        }else {
            throw new DataValidationException("Некорректные ID.");
        }
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        if (followeeId < 0 && filter == null) {
            throw new DataValidationException("Некорректные данные");
        }
        return subscriptionService.getFollowers(followeeId, filter);
    }

    public int getFollowersCount(long followerId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followerId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        return subscriptionService.getFollowing(followerId, filter);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

}

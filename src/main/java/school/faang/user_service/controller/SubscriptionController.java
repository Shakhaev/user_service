package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exeprion.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Нельзя подписаться на самого себя :");
        }
        subscriptionService.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("Нельзя отписаться от самого себя :");
        }
        subscriptionService.unfollowUser(followerId, followeeId);
    }

    public void getFollowers(long followeeId, UserFilterDto filter) {
        subscriptionService.getFollowers(followeeId, filter);
    }
}

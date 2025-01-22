package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@Component
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService service;

    public void followUser(long followerId, long followeeId) throws DataValidationException {
        if (followerId != followeeId) {
            service.followUser(followerId, followeeId);
        } else {
            throw new DataValidationException("Нельзя подписаться на свой аккаунт.");
        }
    }

    public void unfollowUser(long followerId, long followeeId) throws DataValidationException {
        if (followerId != followeeId) {
            service.unfollowUser(followerId, followeeId);
        } else {
            throw new DataValidationException("Нельзя отписаться от самого себя.");
        }
    }
}
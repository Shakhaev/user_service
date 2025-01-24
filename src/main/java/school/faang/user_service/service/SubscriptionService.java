package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository repository;

    public void followUser(long followerId, long followeeId) throws DataValidationException {
        checkingActionsOnYourself(followerId, followeeId, "Нельзя подписаться на свой аккаунт.");

        boolean isThereSub = repository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (isThereSub) {
            throw new DataValidationException("Вы уже подписаны на этого пользователя.");
        }

        repository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) throws DataValidationException {
        checkingActionsOnYourself(followerId, followeeId, "Нельзя отписаться от самого себя.");

        boolean isThereSub = repository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (!isThereSub) {
            throw new DataValidationException("Невозможно отписаться от пользователя, на которого вы не подписаны.");
        }

        repository.unfollowUser(followerId, followeeId);
    }

    private void checkingActionsOnYourself(long followerId, long followeeId, String errorMessage)
            throws DataValidationException {
        if (followerId == followeeId) {
            throw new DataValidationException(errorMessage);
        }
    }
}
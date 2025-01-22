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
        boolean isThereSub = repository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (isThereSub) {
            throw new DataValidationException("Вы уже подписаны на этого пользователя.");
        } else {
            repository.followUser(followerId, followeeId);
        }
    }

    public void unfollowUser(long followerId, long followeeId) throws DataValidationException {
        // пользователь подписан?
        boolean isThereSub = repository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (isThereSub) {
            repository.unfollowUser(followerId, followeeId);
        } else {
            throw new DataValidationException("Невозможно отписаться от пользователя, на которого вы не подписаны.");
        }
    }
}
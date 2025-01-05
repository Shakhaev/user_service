package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException(
                    "FollowerId %d and FolloweeId %d cannot be the same".formatted(followerId, followeeId)
            );
        }
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(
                    "This subscription (%d - %d) already exists".formatted(followerId, followeeId)
            );
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }
}

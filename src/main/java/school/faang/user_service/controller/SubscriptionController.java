package school.faang.user_service.controller;

import org.springframework.stereotype.Component;
import school.faang.user_service.service.SubscriptionService;

@Component
public class SubscriptionController {
    private SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new IllegalArgumentException("You can't subscribe to yourself");
        }

        subscriptionService.followUser(followerId, followeeId);
    }
}

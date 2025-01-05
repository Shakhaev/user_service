package school.faang.user_service.controller;

import org.springframework.stereotype.Component;
import school.faang.user_service.service.SubscriptionService;

@Component
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public void followUser(long followerId, long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }
}

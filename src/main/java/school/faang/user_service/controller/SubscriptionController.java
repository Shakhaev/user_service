package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SubscriptionService;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public void followUser(long followerId, long followeeId) {

        if (followerId == followeeId) {
            throw new DataValidationException("User cannot be follower of himself!");
        }
        subscriptionService.followUser(followerId, followeeId);
    }
}

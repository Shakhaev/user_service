package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import school.faang.user_service.service.SubscriptionService;

@Controller
@RequiredArgsConstructor
public class SubscriptonController {
    private final SubscriptionService subscriptionService;

    /*
        followerId -> id пользователя который хочет подписаться;
        followeeId -> id того на кого хотят подписаться;
     */
    @PostMapping
    public void followerUser(long followerId, long followeeId) {
        subscriptionService.followUser(followerId, followeeId);
    }
}

package school.faang.user_service.service.decorators.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.AppConfig;
import school.faang.user_service.dto.FollowingFeatureDto;
import school.faang.user_service.rating.ActionType;
import school.faang.user_service.service.RatingService;
import school.faang.user_service.service.SubscriptionService;

@Service
public class RatingSubscriptionServiceDecorator extends SubscriptionServiceDecorator {
    private final RatingService ratingService;
    private final AppConfig appConfig;

    public RatingSubscriptionServiceDecorator(

            SubscriptionService subscriptionService, RatingService ratingService, AppConfig appConfig) {
        super(subscriptionService);
        this.ratingService = ratingService;
        this.appConfig = appConfig;
    }

    @Override
    public void followUser(FollowingFeatureDto followingFeatureDTO) {
        super.followUser(followingFeatureDTO);
        addRating(followingFeatureDTO);
    }

    @Override
    public void unfollowUser(FollowingFeatureDto followingFeatureDTO) {
        super.unfollowUser(followingFeatureDTO);
        addRating(followingFeatureDTO);
    }

    private void addRating(FollowingFeatureDto followingFeatureDTO) {
        ratingService.addRating(
                u -> "User : " + u.getUsername() + " -> followed/unfollowed another user and got rating!",
                followingFeatureDTO.followeeId(),
                appConfig.getPassiveTransaction(),
                ActionType.PASSIVE
        );
    }
}

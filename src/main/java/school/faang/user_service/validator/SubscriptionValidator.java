package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Component
public class SubscriptionValidator {
    public void validateFollowerAndFolloweeIds(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You can't subscribe to yourself");
        }
    }
}

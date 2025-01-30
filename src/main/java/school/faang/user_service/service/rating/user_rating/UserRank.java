package school.faang.user_service.service.rating.user_rating;

import school.faang.user_service.dto.rating.UserRatingDto;
import school.faang.user_service.entity.rating.UserRating;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

public interface UserRank {
    boolean isApplicable(UserRatingDto ratings);

    List<UserRating> calculate(List<Long> userIds, UserRatingDto ratings);

    default void validateParameters(List<Long> userIds, UserRatingDto ratings) {
        if (userIds == null || userIds.isEmpty() || ratings == null) {
            throw new DataValidationException("Parameters is not valid");
        }
    }
}

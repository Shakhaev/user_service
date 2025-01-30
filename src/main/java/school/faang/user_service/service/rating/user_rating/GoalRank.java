package school.faang.user_service.service.rating.user_rating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.rating.UserRatingDto;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.rating.UserRating;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.rating.RatingTypeService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class GoalRank implements UserRank {
    private final static String RATING_TYPE_NAME = "Goal rating";

    private final UserService userService;
    private final RatingTypeService ratingTypeService;
    private final GoalService goalService;

    @Override
    public boolean isApplicable(UserRatingDto ratings) {
        return ratings != null && ratings.isGoalRating();
    }

    @Override
    public List<UserRating> calculate(List<Long> userIds, UserRatingDto ratings) {
        validateParameters(userIds, ratings);

        UserRatingType ratingType = ratingTypeService.findByName(RATING_TYPE_NAME);
        GoalFilterDto goalFilterDto = new GoalFilterDto();
        goalFilterDto.setStatus(GoalStatus.COMPLETED);

        return userIds.stream()
                .map(userId -> {
                    int goalsCount = goalService.getGoalsByUserId(userId, goalFilterDto).size();
                    int score = goalsCount * ratingType.getCost();

                    return UserRating.builder()
                            .user(userService.getUser(userId))
                            .type(ratingType)
                            .score(score)
                            .build();
                })
                .toList();
    }
}

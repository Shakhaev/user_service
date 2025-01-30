package school.faang.user_service.service.rating.user_rating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.rating.UserRatingDto;
import school.faang.user_service.entity.rating.UserRating;
import school.faang.user_service.entity.rating.UserRatingType;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.rating.RatingTypeService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class SkillRank implements UserRank {
    private final static String RATING_TYPE_NAME = "Skill rating";

    private final UserService userService;
    private final RatingTypeService ratingTypeService;
    private final SkillService skillService;

    @Override
    public boolean isApplicable(UserRatingDto ratings) {
        return ratings != null && ratings.isSkillRating();
    }

    @Override
    public List<UserRating> calculate(List<Long> userIds, UserRatingDto ratings) {
        validateParameters(userIds, ratings);

        UserRatingType ratingType = ratingTypeService.findByName(RATING_TYPE_NAME);

        return userIds.stream()
                .map(userId -> {
                    int skillServiceCount = skillService.getUserSkills(userId).size();
                    int score = skillServiceCount * ratingType.getCost();

                    return UserRating.builder()
                            .user(userService.getUser(userId))
                            .type(ratingType)
                            .score(score)
                            .build();
                })
                .toList();
    }
}

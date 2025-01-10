package school.faang.user_service.leaderboard;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserScoreDto;

@Component
public class RecommendationsReceivedCalculator implements ScoreCalculator {
    public static final int ACTIVITY_SCORE_INDEX = 3;

    @Override
    public int getScore(@NotNull UserScoreDto userDto) {
        return userDto.getRecommendationsReceivedId().size() * ACTIVITY_SCORE_INDEX;
    }
}

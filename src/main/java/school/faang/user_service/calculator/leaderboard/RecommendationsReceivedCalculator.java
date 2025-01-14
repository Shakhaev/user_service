package school.faang.user_service.calculator.leaderboard;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

@Component
public class RecommendationsReceivedCalculator implements LeaderboardCalculator {
    public static final int ACTIVITY_SCORE_INDEX = 3;

    @Override
    public int getScore(@NotNull LeaderboardDto userDto) {
        return userDto.getRecommendationsReceivedId().size() * ACTIVITY_SCORE_INDEX;
    }
}

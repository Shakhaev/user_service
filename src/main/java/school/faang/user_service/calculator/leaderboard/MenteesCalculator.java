package school.faang.user_service.calculator.leaderboard;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

@Component
public class MenteesCalculator implements LeaderboardCalculator {
    public static final int ACTIVITY_SCORE_INDEX = 4;

    @Override
    public int getScore(@NotNull LeaderboardDto userDto) {
        return userDto.getMenteesId().size() * ACTIVITY_SCORE_INDEX;
    }
}

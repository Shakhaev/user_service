package school.faang.user_service.calculator.leaderboard;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

@Component
public class OwnedEventsCalculator implements LeaderboardCalculator {
    public static final int ACTIVITY_SCORE_INDEX = 2;

    @Override
    public int getScore(@NotNull LeaderboardDto userDto) {
        return userDto.getOwnedEventsId().size() * ACTIVITY_SCORE_INDEX;
    }
}

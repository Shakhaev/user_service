package school.faang.user_service.calculator.leaderboard;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

@Component
public class GoalsCalculator implements LeaderboardCalculator {
    @Getter
    private static final int ACTIVITY_SCORE_INDEX = 2;

    @Override
    public int getScore(@NotNull LeaderboardDto userDto) {
        return userDto.getGoalsId().size() * ACTIVITY_SCORE_INDEX;
    }
}

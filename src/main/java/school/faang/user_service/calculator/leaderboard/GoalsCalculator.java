package school.faang.user_service.calculator.leaderboard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

import java.util.Optional;

@Component
public class GoalsCalculator implements LeaderboardCalculator {
    @Getter
    private final int ACTIVITY_SCORE_INDEX = 2;

    @Override
    public int getScore(@Valid @NotNull LeaderboardDto leaderboardDto) {
        return Optional.ofNullable(leaderboardDto.getGoalsId())
                .map(goals -> goals.size() * ACTIVITY_SCORE_INDEX)
                .orElse(0);
    }
}

package school.faang.user_service.calculator.leaderboard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

public interface LeaderboardCalculator {

    int getScore(@Valid @NotNull LeaderboardDto leaderboardDto);
}

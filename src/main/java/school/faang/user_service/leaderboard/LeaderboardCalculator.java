package school.faang.user_service.leaderboard;

import jakarta.validation.constraints.NotNull;
import school.faang.user_service.dto.user.LeaderboardDto;

public interface LeaderboardCalculator {

    int getScore(@NotNull LeaderboardDto userDto);
}

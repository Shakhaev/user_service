package school.faang.user_service.leaderboard;

import jakarta.validation.constraints.NotNull;
import school.faang.user_service.dto.user.UserScoreDto;

public interface ScoreCalculator {

    long getScore(@NotNull UserScoreDto userDto);
}

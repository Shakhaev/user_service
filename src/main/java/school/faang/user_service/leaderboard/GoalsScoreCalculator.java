package school.faang.user_service.leaderboard;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserScoreDto;

@Component
public class GoalsScoreCalculator implements ScoreCalculator {
    public static final int ACTIVITY_SCORE_INDEX = 2;

    @Override
    public int getScore(@NotNull UserScoreDto userDto) {
        return userDto.getGoalsId().size() * ACTIVITY_SCORE_INDEX;
    }
}

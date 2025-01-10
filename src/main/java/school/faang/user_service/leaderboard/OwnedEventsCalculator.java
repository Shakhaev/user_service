package school.faang.user_service.leaderboard;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserScoreDto;

@Component
public class OwnedEventsCalculator implements ScoreCalculator {
    public static final int ACTIVITY_SCORE_INDEX = 2;

    @Override
    public int getScore(@NotNull UserScoreDto userDto) {
        return userDto.getOwnedEventsId().size() * ACTIVITY_SCORE_INDEX;
    }
}

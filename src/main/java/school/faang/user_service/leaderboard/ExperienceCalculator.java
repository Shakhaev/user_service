package school.faang.user_service.leaderboard;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserScoreDto;

@Component
public class ExperienceCalculator implements ScoreCalculator {
    @Override
    public int getScore(@NotNull UserScoreDto userDto) {
        return userDto.getExperience();
    }
}

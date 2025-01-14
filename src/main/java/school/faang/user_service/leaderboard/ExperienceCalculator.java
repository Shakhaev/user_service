package school.faang.user_service.leaderboard;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.LeaderboardDto;

@Component
public class ExperienceCalculator implements LeaderboardCalculator {
    @Override
    public int getScore(@NotNull LeaderboardDto userDto) {
        return userDto.getExperience();
    }
}

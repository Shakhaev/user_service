package school.faang.user_service.calculator.leaderboard;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

@Component
public class SkillsCalculator implements LeaderboardCalculator {

    @Override
    public int getScore(@NotNull LeaderboardDto userDto) {
        return userDto.getSkillsId().size();

    }
}

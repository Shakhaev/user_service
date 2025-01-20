package school.faang.user_service.calculator.leaderboard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

import java.util.List;
import java.util.Optional;

@Component
public class SkillsCalculator implements LeaderboardCalculator {

    @Override
    public int getScore(@Valid @NotNull LeaderboardDto leaderboardDto) {
        return Optional.ofNullable(leaderboardDto.getSkillsId())
                .map(List::size)
                .orElse(0);
    }
}

package school.faang.user_service.calculator.leaderboard;

import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

import java.util.Optional;

@Component
public class ExperienceCalculator implements LeaderboardCalculator {

    @Override
    public int getScore(@Valid @NotNull LeaderboardDto leaderboardDto) {
        return Optional.ofNullable(leaderboardDto.getExperience())
                .orElse(0);
    }
}

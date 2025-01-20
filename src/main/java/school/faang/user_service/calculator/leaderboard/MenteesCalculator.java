package school.faang.user_service.calculator.leaderboard;

import jakarta.validation.Valid;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

import java.util.Optional;

@Component
public class MenteesCalculator implements LeaderboardCalculator {
    @Getter
    private final int ACTIVITY_SCORE_INDEX = 4;

    @Override
    public int getScore(@Valid @NotNull LeaderboardDto leaderboardDto) {
        return Optional.ofNullable(leaderboardDto.getMenteesId())
                .map(mentees -> mentees.size() * ACTIVITY_SCORE_INDEX)
                .orElse(0);
    }
}

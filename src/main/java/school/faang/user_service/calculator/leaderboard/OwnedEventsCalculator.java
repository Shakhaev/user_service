package school.faang.user_service.calculator.leaderboard;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

import java.util.Optional;

@Component
public class OwnedEventsCalculator implements LeaderboardCalculator {
    @Getter
    private final int ACTIVITY_SCORE_INDEX = 2;

    @Override
    public int getScore(@Valid @NotNull LeaderboardDto leaderboardDto) {
        return Optional.ofNullable(leaderboardDto.getOwnedEventsId())
                .map(events -> events.size() * ACTIVITY_SCORE_INDEX)
                .orElse(0);
    }
}

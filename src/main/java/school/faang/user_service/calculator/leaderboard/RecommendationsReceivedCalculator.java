package school.faang.user_service.calculator.leaderboard;

import jakarta.validation.Valid;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

import java.util.Optional;

@Component
public class RecommendationsReceivedCalculator implements LeaderboardCalculator {
    @Getter
    private final int ACTIVITY_SCORE_INDEX = 3;

    @Override
    public int getScore(@Valid @NotNull LeaderboardDto leaderboardDto) {
        return Optional.ofNullable(leaderboardDto.getRecommendationsReceivedId())
                .map(recommendations -> recommendations.size() * ACTIVITY_SCORE_INDEX)
                .orElse(0);
    }
}

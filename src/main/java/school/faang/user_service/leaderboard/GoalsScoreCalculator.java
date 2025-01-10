package school.faang.user_service.leaderboard;

import jakarta.validation.constraints.NotNull;
import school.faang.user_service.dto.user.UserScoreDto;
import school.faang.user_service.repository.UserRepository;

public class GoalsScoreCalculator implements ScoreCalculator {
    private UserRepository userRepository;

    @Override
    public long getScore(@NotNull UserScoreDto userDto) {
        return userDto.getGoalsId().size();
    }
}

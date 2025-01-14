package school.faang.user_service.leaderboard;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.LeaderboardDto;
import school.faang.user_service.repository.UserRepository;

@Component
public class SkillsCalculator implements LeaderboardCalculator {
    private UserRepository userRepository;

    @Override
    public int getScore(@NotNull LeaderboardDto userDto) {
        return userDto.getSkillsId().size();

    }
}

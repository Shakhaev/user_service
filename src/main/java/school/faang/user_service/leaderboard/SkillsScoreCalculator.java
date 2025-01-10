package school.faang.user_service.leaderboard;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserScoreDto;
import school.faang.user_service.repository.UserRepository;

@Component
public class SkillsScoreCalculator implements ScoreCalculator {
    private UserRepository userRepository;

    @Override
    public int getScore(@NotNull UserScoreDto userDto) {
        return userDto.getSkillsId().size();

    }
}

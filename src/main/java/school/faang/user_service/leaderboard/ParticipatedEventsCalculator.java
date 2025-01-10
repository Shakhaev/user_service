package school.faang.user_service.leaderboard;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserScoreDto;

@Component
public class ParticipatedEventsCalculator implements ScoreCalculator {
    @Override
    public int getScore(UserScoreDto userDto) {
        return userDto.getParticipatedEventsId().size();
    }
}

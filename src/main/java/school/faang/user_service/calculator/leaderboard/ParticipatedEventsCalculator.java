package school.faang.user_service.calculator.leaderboard;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;

@Component
public class ParticipatedEventsCalculator implements LeaderboardCalculator {
    @Override
    public int getScore(LeaderboardDto userDto) {
        return userDto.getParticipatedEventsId().size();
    }
}

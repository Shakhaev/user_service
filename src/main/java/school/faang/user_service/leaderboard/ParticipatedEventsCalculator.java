package school.faang.user_service.leaderboard;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.LeaderboardDto;

@Component
public class ParticipatedEventsCalculator implements LeaderboardCalculator {
    @Override
    public int getScore(LeaderboardDto userDto) {
        return userDto.getParticipatedEventsId().size();
    }
}

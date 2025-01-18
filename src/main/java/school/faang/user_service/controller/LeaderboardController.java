package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;
import school.faang.user_service.service.LeaderboardService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    public List<LeaderboardDto> getLeaderboardStream() {
        return leaderboardService.getUsersLeaderboard();
    }
}

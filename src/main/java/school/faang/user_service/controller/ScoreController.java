package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserScoreDto;
import school.faang.user_service.service.ScoreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScoreController {
    private final ScoreService scoreService;

    public List<UserScoreDto> getLeaderboardStream() {
        return scoreService.getUsersLeaderboard();
    }
}

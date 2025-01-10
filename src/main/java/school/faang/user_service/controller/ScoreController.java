package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import school.faang.user_service.service.ScoreService;

import java.time.Duration;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ScoreController {
    public static final int UPDATE_DELAY = 5;
    private final ScoreService scoreService;

    public Flux<Map<Long, Long>> getLeaderboardStream() {
        return Flux.interval(Duration.ofSeconds(UPDATE_DELAY))
                .map(tick -> scoreService.getUsersLeaderboard())
                .publish()
                .autoConnect();
    }
}

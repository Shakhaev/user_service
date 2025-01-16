package school.faang.user_service.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisCleanupScheduler {
    private final RedisTemplate<String, Object> redisTemplate;

    @Scheduled(cron = "0 0 */12 * * ?")
    public void clearLeaderboardData() {
        redisTemplate.delete("leaderboard:last12hours");
    }
}

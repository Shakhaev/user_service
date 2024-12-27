package school.faang.user_service.publisher;

import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.event.GoalCompletedEvent;
import school.faang.user_service.exception.RedisPublishingException;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalCompletedEventPublisher implements EventPublisher<GoalCompletedEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    @Retryable(
            retryFor = {RedisConnectionException.class, RedisPublishingException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void publish(GoalCompletedEvent event) {
        redisTemplate.convertAndSend(redisProperties.channel().goalChannel(), event);
        log.info("Message sent to channel: {}", redisProperties.channel().goalChannel());
    }

    @Override
    public Class<GoalCompletedEvent> getEventClass() {
        return GoalCompletedEvent.class;
    }
}

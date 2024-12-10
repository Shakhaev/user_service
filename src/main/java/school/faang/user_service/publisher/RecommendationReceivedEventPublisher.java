package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.RetryProperties;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.event.RecommendationReceivedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationReceivedEventPublisher implements EventPublisher<RecommendationReceivedEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RetryProperties retryProperties;
    private final RedisProperties redisProperties;

    @Override
    @Retryable(retryFor = Exception.class,
            maxAttemptsExpression = "#{@retryProperties.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "#{@retryProperties.initialDelay}",
                    multiplierExpression = "#{@retryProperties.multiplier}",
                    maxDelayExpression = "#{@retryProperties.maxDelay}"
            )
    )
    public void publish(RecommendationReceivedEvent event) {
        redisTemplate.convertAndSend(redisProperties.getChannel().getRecommendationChannel(), event);
        log.info("Message sent to channel: {}", redisProperties.getChannel().getRecommendationChannel());
    }
}

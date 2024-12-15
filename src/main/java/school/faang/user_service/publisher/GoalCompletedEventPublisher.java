package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.event.GoalCompletedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalCompletedEventPublisher implements EventPublisher<GoalCompletedEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    @Override
    public void publish(GoalCompletedEvent event) {
        redisTemplate.convertAndSend(redisProperties.getChannel().getGoalChannel(), event);
        log.info("Message sent to channel: {}", redisProperties.getChannel().getGoalChannel());
    }
}

package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.event.EventStartEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventStartEventPublisher implements EventPublisher<EventStartEvent> {
    private final RedisTemplate<String, Object> lettuceRedisTemplate;
    private final RedisProperties redisProperties;

    @Override
    public void publish(EventStartEvent event) {
        lettuceRedisTemplate.convertAndSend(redisProperties.getChannel().getEventStartEventChannel(), event);
        log.info("Event start event sent to channel: {}", redisProperties.getChannel().getEventStartEventChannel());
    }
}

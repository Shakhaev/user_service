package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.GoalCompletedEvent;

@Component
@RequiredArgsConstructor
public class GoalCompletedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.goal-completed-channel.name}")
    private String topic;

    public void publish(GoalCompletedEvent event) {
        redisTemplate.convertAndSend(topic, event);
    }
}

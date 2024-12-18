package school.faang.user_service.redis.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.redis.event.RecommendationEvent;

@Component
@RequiredArgsConstructor
public class RecommendationEventPublisher implements EventPublisher<RecommendationEvent> {

    @Value("${spring.data.redis.channel.recommendation}")
    private String recommendationChannel;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(RecommendationEvent event) {
        redisTemplate.convertAndSend(recommendationChannel, event);
    }
}

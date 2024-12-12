package school.faang.user_service.config.redis.publisher;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PremiumEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topicEventParticipation;

    public void publishEvent(Object event) {
        redisTemplate.convertAndSend(topicEventParticipation.getTopic(), event);
        log.info("Published event to Redis topic {}: {}", topicEventParticipation.getTopic(), event);
    }
}

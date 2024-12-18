package school.faang.user_service.publisher;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.PremiumBoughtEvent;

@Service
@Slf4j
@RequiredArgsConstructor
@AllArgsConstructor
public class PremiumBoughtEventPublisher {
    private RedisTemplate<String, Object> redisTemplate;
    private ChannelTopic buyPremiumTopic;

    public void publishPremiumBoughtEvent(PremiumBoughtEvent premiumBoughtEvent) {
        log.info("Converting to redis new event: {}", premiumBoughtEvent.toString());
        try {
            redisTemplate.convertAndSend(buyPremiumTopic.getTopic(), premiumBoughtEvent);
        } catch (RuntimeException e) {
            log.error("Failed to publish event to Redis: {}", e.getMessage());
        }
    }
}

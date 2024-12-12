package school.faang.user_service.config.redis.events.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.events.PremiumBoughtEvent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class PremiumBoughtEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic premiumBoughtTopic;

    public void publish(Long userId, double amount, int duration) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("userId", userId);
            event.put("amount", amount);
            event.put("duration", duration);
            event.put("timestamp", LocalDateTime.now().toString());

            redisTemplate.convertAndSend(premiumBoughtTopic.getTopic(), event);
            log.info("Published event to Redis topic '{}': {}", premiumBoughtTopic.getTopic(), event);
        } catch (Exception e) {
            log.error("Failed to publish event: {}", e.getMessage());
        }
    }
    public void buyPremium(Long userId, double amount, int duration) {
        log.info("Processing premium purchase for user {}: amount {}, duration {}", userId, amount, duration);
        eventPublisher.publish(userId, amount, duration);
    }

}


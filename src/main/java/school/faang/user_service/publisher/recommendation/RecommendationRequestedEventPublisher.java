package school.faang.user_service.publisher.recommendation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationRequestedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.recommendation-requested-channel}")
    private String channel;

    public void publish(RecommendationRequestedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channel, json);
            log.info("The {} was successfully published on the channel {}", event, channel);
        } catch (JsonProcessingException e) {
            log.error("Error converting object {} to JSON", event, e);
            throw new RuntimeException(e);
        }
    }
}

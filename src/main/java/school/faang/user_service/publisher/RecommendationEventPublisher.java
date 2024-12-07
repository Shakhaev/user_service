package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationEvent;

@Component
@RequiredArgsConstructor
public class RecommendationEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(RecommendationEvent event) {
        String recommendationEventJson = null;
        try {
            recommendationEventJson = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend("recommendations", recommendationEventJson);
    }
}

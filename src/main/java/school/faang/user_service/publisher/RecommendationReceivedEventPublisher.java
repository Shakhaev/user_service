package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationReceivedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationReceivedEventPublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic recommendationTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(Long recommendationId) {
        RecommendationReceivedEvent event = new RecommendationReceivedEvent();
        event.setId(recommendationId);
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(recommendationTopic.getTopic(), json);
        } catch (JsonProcessingException e) {
            log.error("json processing error " + e);
            throw new RuntimeException(e);
        }
    }
}

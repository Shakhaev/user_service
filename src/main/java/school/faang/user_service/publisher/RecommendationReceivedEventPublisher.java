package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.publisher.RecommendationReceivedEventDto;

@Component
@RequiredArgsConstructor
public class RecommendationReceivedEventPublisher {

    @Value("${spring.data.redis.channels.recommendation-channel.name}")
    private String channelRecommendation;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(RecommendationReceivedEventDto recommendationReceivedEventDto) {
        try {
            String json = objectMapper.writeValueAsString(recommendationReceivedEventDto);
            redisTemplate.convertAndSend(channelRecommendation, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.events.RecommendationReceivedEventDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationReceivedEventPublisher implements MessagePublisher<RecommendationReceivedEventDto>{

    @Value("${spring.data.redis.channels.recommendation-channel.name}")
    private String channelRecommendation;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(RecommendationReceivedEventDto event) {
        try {
            log.info("Публикация в redis события RecommendationReceivedEventDto");
            String json = objectMapper.writeValueAsString(event);
            log.debug("RecommendationReceivedEventDto: {}", json);
            redisTemplate.convertAndSend(channelRecommendation, json);
        } catch (JsonProcessingException e) {
            log.error("Не удалось конвертировать объект RecommendationEventDto={} в строку", event, e);
            throw new RuntimeException(e);
        }
    }
}

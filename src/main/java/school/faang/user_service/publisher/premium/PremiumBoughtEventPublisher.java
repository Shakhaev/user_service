package school.faang.user_service.publisher.premium;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.premium.PremiumBoughtEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class PremiumBoughtEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    @Value("${spring.data.redis.channels.premium-bought-channel.name}")
    private String premiumBoughtChannel;

    public void publish(PremiumBoughtEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(premiumBoughtChannel, json);
        } catch (JsonProcessingException e) {
            log.error("Error converting object {} to JSON: {}", event, e.getMessage());
            throw new IllegalStateException(e);
        }
    }
}

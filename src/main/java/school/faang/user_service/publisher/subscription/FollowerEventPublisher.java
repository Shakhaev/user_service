package school.faang.user_service.publisher.subscription;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.FollowerEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowerEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.follower-event-channel.name}")
    private String followersChannel;

    public void publish(FollowerEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(followersChannel, json);
        } catch (JsonProcessingException e) {
            log.error("Error converting object {} to JSON: {}", event, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

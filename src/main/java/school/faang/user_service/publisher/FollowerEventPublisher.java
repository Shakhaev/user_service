package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.FollowerEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowerEventPublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.follower-event-channel.name}")
    private String followerTopic;

    public void publish(FollowerEvent followerEvent) {
        try {
            String message = objectMapper.writeValueAsString(followerEvent);
            redisTemplate.convertAndSend(followerTopic, message);
            log.info("Published event to topic {}: {}", followerTopic, message);
        } catch (Exception e) {
            log.error("Failed to publish event to topic {}: {}", followerTopic, e.getMessage(), e);
        }
    }
}

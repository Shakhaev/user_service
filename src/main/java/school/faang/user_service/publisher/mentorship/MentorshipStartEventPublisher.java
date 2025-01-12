package school.faang.user_service.publisher.mentorship;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentroship.MentorshipStartEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipStartEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.mentorship-channel}")
    private String mentorshipChannel;

    public void publish(MentorshipStartEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(mentorshipChannel, json);
        } catch (JsonProcessingException e) {
            log.error("Error converting object {} to JSON: {}", event, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

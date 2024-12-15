package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.MentorshipAcceptedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipAcceptedEventPublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChannelTopic mentorshipAcceptedTopic;

    public void publish(MentorshipAcceptedEvent event) {
        try {
            redisTemplate.convertAndSend(mentorshipAcceptedTopic.getTopic(), objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error("Json processing exception", e);
            throw new RuntimeException(e.getMessage());
        }


    }
}

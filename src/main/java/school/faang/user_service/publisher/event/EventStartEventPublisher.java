package school.faang.user_service.publisher.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventStartEvent;
import school.faang.user_service.publisher.EventPublisherAbstract;

@Slf4j
@Component
public class EventStartEventPublisher extends EventPublisherAbstract<EventStartEvent> {
    @Value("${spring.data.redis.channels.event-start-event-channel}")
    private String eventStartEventTopic;

    public EventStartEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    public void publish(EventStartEvent eventStartEvent) {
        handleEvent(eventStartEvent, eventStartEventTopic);
    }
}


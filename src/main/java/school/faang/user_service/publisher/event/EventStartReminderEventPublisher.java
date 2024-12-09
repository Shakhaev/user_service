package school.faang.user_service.publisher.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventStartReminderEvent;
import school.faang.user_service.publisher.EventPublisherAbstract;

@Slf4j
@Component
public class EventStartReminderEventPublisher extends EventPublisherAbstract<EventStartReminderEvent> {
    @Value("${spring.data.redis.channels.event-start-reminder-event-channel}")
    private String eventStartReminderEventTopic;

    public EventStartReminderEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    public void publish(EventStartReminderEvent event) {
        handleEvent(event, eventStartReminderEventTopic);
    }
}

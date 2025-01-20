package school.faang.user_service.message.broker.publisher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.message.EventStartMessage;
import school.faang.user_service.dto.event.message.builder.EventStartMessageBuilder;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.enums.publisher.PublisherType;

import java.util.List;

import static school.faang.user_service.enums.publisher.PublisherType.EVENT_START;

@Getter
@RequiredArgsConstructor
@Component
public class EventStartEventToKafkaPublisher implements EventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final EventStartMessageBuilder builder;
    private final PublisherType type = EVENT_START;

    @Value("${spring.kafka.topic.event.start}")
    private String topicName;

    @Override
    public void publish(JoinPoint joinPoint, Object returnedValue) {
        List<Event> events;

        if (returnedValue instanceof List) {
            events = (List<Event>) returnedValue;
        } else {
            events = List.of((Event) returnedValue);
        }

        List<EventStartMessage> messages = events.stream()
                .map(builder::build)
                .toList();

        messages.forEach(message -> kafkaTemplate.send(topicName, message));
    }
}

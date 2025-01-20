package school.faang.user_service.message.broker.publisher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship_request.message.MentorshipRequestAcceptedMessage;
import school.faang.user_service.dto.mentorship_request.message.builder.MentorshipRequestAcceptedMessageBuilder;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.enums.publisher.PublisherType;

import static school.faang.user_service.enums.publisher.PublisherType.MENTORSHIP_REQUEST_ACCEPTED;

@Getter
@RequiredArgsConstructor
@Component
public class MentorshipRequestAcceptedEventToKafkaPublisher implements EventPublisher {
    private final PublisherType type = MENTORSHIP_REQUEST_ACCEPTED;
    private final MentorshipRequestAcceptedMessageBuilder builder;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.user.mentorship_request.accepted}")
    private String topicName;

    @Override
    public void publish(JoinPoint joinPoint, Object returnedValue) {
        MentorshipRequest request = (MentorshipRequest) returnedValue;

        MentorshipRequestAcceptedMessage message = builder.build(request);

        kafkaTemplate.send(topicName, message);
    }
}

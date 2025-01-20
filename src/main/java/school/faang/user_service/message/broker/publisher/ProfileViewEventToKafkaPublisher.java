package school.faang.user_service.message.broker.publisher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.message.ProfileViewEventParticipant;
import school.faang.user_service.dto.user.message.ProfileViewMessage;
import school.faang.user_service.dto.user.message.builder.ProfileViewMessageBuilder;
import school.faang.user_service.enums.publisher.PublisherType;

import java.util.List;

import static school.faang.user_service.enums.publisher.PublisherType.PROFILE_VIEW;

@Getter
@RequiredArgsConstructor
@Component
public class ProfileViewEventToKafkaPublisher implements EventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PublisherType type = PROFILE_VIEW;
    private final ProfileViewMessageBuilder builder;
    private final UserContext userContext;

    @Value("${spring.kafka.topic.user.profile_view}")
    private String topicName;

    @Override
    public void publish(JoinPoint joinPoint, Object returnedValue) {
        if (returnedValue == null) {
            return;
        }

        Long receiverId = userContext.getUserId();
        ProfileViewMessage message;

        if (returnedValue instanceof List) {
            message = builder.build(receiverId, (List<ProfileViewEventParticipant>) returnedValue);
        } else {
            message = builder.build(receiverId, List.of((ProfileViewEventParticipant) returnedValue));
        }

        kafkaTemplate.send(topicName, message);
    }
}

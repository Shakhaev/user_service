package school.faang.user_service.message.broker.publisher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.message.NewFollowerMessage;
import school.faang.user_service.entity.User;
import school.faang.user_service.enums.publisher.PublisherType;
import school.faang.user_service.service.user.UserDomainService;

import static school.faang.user_service.enums.publisher.PublisherType.NEW_FOLLOWER;

@Getter
@RequiredArgsConstructor
@Component
public class NewFollowerEventToKafkaPublisher implements EventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserDomainService userDomainService;
    private final PublisherType type = NEW_FOLLOWER;

    @Value("${spring.kafka.topic.user.new_follower}")
    private String topicName;

    @Override
    public void publish(JoinPoint joinPoint, Object returnedValue) {
        Object[] ids = joinPoint.getArgs();
        Long followerId = (Long) ids[0];
        Long followeeId = (Long) ids[1];

        User follower = userDomainService.findById(followerId);

        NewFollowerMessage message = NewFollowerMessage.builder()
                .followeeId(followeeId)
                .followerId(followerId)
                .followerName(follower.getUsername())
                .build();

        kafkaTemplate.send(topicName, message);
    }
}

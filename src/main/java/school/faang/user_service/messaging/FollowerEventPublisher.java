package school.faang.user_service.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.messaging.ProjectFollowerEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class FollowerEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;


    public void publish(ProjectFollowerEvent message) {
        log.info("Publishing message to topic: {}, message: {}", topic.getTopic(), message);
        Long status = redisTemplate.convertAndSend(topic.getTopic(), message);
        log.info("Published message to topic: {}, error status: {}", topic.getTopic(), status);
    }
}

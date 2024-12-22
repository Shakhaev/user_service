package school.faang.user_service.publisher.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.event.goal.GoalSetEvent;
import school.faang.user_service.publisher.MessagePublisher;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalEventPublisher implements MessagePublisher<GoalSetEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic goalSetTopic;

    @Override
    public void publish(GoalSetEvent message) {
        redisTemplate.convertAndSend(goalSetTopic.getTopic(), message);
        log.info("Message was send {}, in topic - {}", message, goalSetTopic.getTopic());
    }
}

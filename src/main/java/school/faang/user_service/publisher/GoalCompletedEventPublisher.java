package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalCompletedEventDto;

@Component
@Slf4j
@RequiredArgsConstructor
public class GoalCompletedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    @Qualifier("goalCompletedChannel")
    private final ChannelTopic goalCompletedChannel;

    public void publish(GoalCompletedEventDto event) {
        log.info("The goal completion event has been published to the , {} , channel", goalCompletedChannel);
        redisTemplate.convertAndSend(goalCompletedChannel.getTopic(), event);
    }
}

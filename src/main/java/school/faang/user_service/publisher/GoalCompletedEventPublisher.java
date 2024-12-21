package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalCompletedEventDto;

@Component
@Slf4j
@RequiredArgsConstructor
public class GoalCompletedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic goalCompletedChannel;
    private final ObjectMapper objectMapper;

    public void publish(GoalCompletedEventDto event) {
        log.info("The goal completion event has been published to the , {} , channel", goalCompletedChannel);
        try {
            redisTemplate.convertAndSend(goalCompletedChannel.getTopic(), event);
        } catch (RuntimeException e) {
            log.error("Could not publish the event with Goal ID, {}", event.getGoalId());
            throw new RuntimeException(e.getMessage());
        }
    }
}

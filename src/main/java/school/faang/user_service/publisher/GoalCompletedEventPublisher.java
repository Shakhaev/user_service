package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalCompletedEventDto;

@Component
@RequiredArgsConstructor
public class GoalCompletedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    @Qualifier("goalCompletedChannel")
    private final ChannelTopic goalCompletedEventChannel;

    public void publish(GoalCompletedEventDto message) {
        redisTemplate.convertAndSend(goalCompletedEventChannel.getTopic(), message);
    }
}

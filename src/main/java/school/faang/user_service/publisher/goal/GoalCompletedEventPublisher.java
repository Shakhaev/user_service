package school.faang.user_service.publisher.goal;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalCompletedEvent;
import school.faang.user_service.publisher.EventPublisherAbstract;

@Component
@Slf4j
public class GoalCompletedEventPublisher extends EventPublisherAbstract<GoalCompletedEvent> {
    @Value("${spring.data.redis.channels.goal-completed-channel}")
    private String topicGoalCompleted;

    public GoalCompletedEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    public void publish(GoalCompletedEvent event) {
        handleEvent(event, topicGoalCompleted);
    }
}

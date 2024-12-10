package school.faang.user_service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.events.GoalCompletedEvent;
import school.faang.user_service.events.SkillAcquiredEvent;

@ExtendWith(MockitoExtension.class)
class SkillAcquiredEventPublisherTest {
    @InjectMocks
    private SkillAcquiredEventPublisher skillAcquiredEventPublisher;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic goalCompletedTopic;
    private String topic = "topic";

    @Test
    void testSuccessfulSendingMessage() {
        SkillAcquiredEvent skillAcquiredEvent = new SkillAcquiredEvent(1,2);

        Mockito.when(goalCompletedTopic.getTopic()).thenReturn(topic);

        skillAcquiredEventPublisher.publish(skillAcquiredEvent);

        Mockito.verify(redisTemplate).convertAndSend(topic, skillAcquiredEvent);
    }
}
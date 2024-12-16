package school.faang.user_service.publisher;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.events.SkillAcquiredEvent;
@Component
public class SkillAcquiredEventPublisher extends AbstractEventPublisher<SkillAcquiredEvent>{
    public SkillAcquiredEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic skillAcquiredTopic) {
        super(redisTemplate, skillAcquiredTopic);
    }

    @Override
    public Class<SkillAcquiredEvent> getInstance() {
        return SkillAcquiredEvent.class;
    }
}

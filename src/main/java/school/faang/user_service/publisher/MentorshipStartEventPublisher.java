package school.faang.user_service.publisher;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.events.MentorshipStartEvent;
@Component
public class MentorshipStartEventPublisher extends AbstractEventPublisher<MentorshipStartEvent> {
    public MentorshipStartEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic mentorshipEventTopic) {
        super(redisTemplate, mentorshipEventTopic);
    }

    @Override
    public Class<MentorshipStartEvent> getInstance() {
        return MentorshipStartEvent.class;
    }
}

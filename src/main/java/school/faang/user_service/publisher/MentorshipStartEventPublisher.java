package school.faang.user_service.publisher;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.events.MentorshipStartEvent;

public class MentorshipStartEventPublisher extends AbstractEventPublisher<MentorshipStartEvent> {
    public MentorshipStartEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic mentorshipEventTopic) {
        super(redisTemplate, mentorshipEventTopic);
    }

    @Override
    public Class<?> getInstance() {
        return MentorshipStartEvent.class;
    }
}

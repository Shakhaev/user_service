package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.events.MentorshipAcceptedEvent;

@Slf4j
@Component
public class MentorshipAcceptedEventPublisher extends AbstractEventPublisher<MentorshipAcceptedEvent>{
    public MentorshipAcceptedEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic mentorshipAcceptedTopic) {
        super(redisTemplate, mentorshipAcceptedTopic);
    }

    @Override
    public Class<MentorshipAcceptedEvent> getInstance() {
        return MentorshipAcceptedEvent.class;
    }
}

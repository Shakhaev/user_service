package school.faang.user_service.publisher.mentorshipoffered;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.event.ban.MentorshipOfferedEvent;
import school.faang.user_service.publisher.MessagePublisher;

@Log4j2
@Service
@RequiredArgsConstructor
public class MentorshipOfferedPublisher implements MessagePublisher<MentorshipOfferedEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic mentorshipOfferedTopic;

    @Override
    public void publish(MentorshipOfferedEvent message) {
        redisTemplate.convertAndSend(mentorshipOfferedTopic.getTopic(), message);

    }
}
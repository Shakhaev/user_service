package school.faang.user_service.redis.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.redis.event.MentorshipRequestedEvent;

@Component
@RequiredArgsConstructor
public class MentorshipRequestedEventPublisher implements EventPublisher<MentorshipRequestedEvent> {

    @Value("${spring.data.redis.channel.mentorship.requested}")
    private String mentorshipRequestedChannel;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(MentorshipRequestedEvent event) {
        redisTemplate.convertAndSend(mentorshipRequestedChannel, event);
    }
}

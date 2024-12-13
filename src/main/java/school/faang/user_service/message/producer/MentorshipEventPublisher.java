package school.faang.user_service.message.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import school.faang.user_service.message.event.MentorshipEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorshipEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.mentorship-channel.name}")
    private String mentorshipEventChannel;

    @Retryable(maxAttempts = 5, backoff = @Backoff(multiplier = 2.0))
    public void publish(MentorshipEvent mentorshipEvent) {
        log.info("Trying to send mentorshipEvent {}", mentorshipEvent);
        redisTemplate.convertAndSend(mentorshipEventChannel, mentorshipEvent);
    }
}

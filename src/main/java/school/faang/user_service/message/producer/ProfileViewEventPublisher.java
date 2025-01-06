package school.faang.user_service.message.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import school.faang.user_service.message.event.ProfileViewEvent;

@Component
@RequiredArgsConstructor
public class ProfileViewEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.profile-view-channel.name}")
    private String profileViewChannel;

    @Retryable(maxAttempts = 5, backoff = @Backoff(multiplier = 2.0))
    public void publish(ProfileViewEvent profileViewEvent) {
        redisTemplate.convertAndSend(profileViewChannel, profileViewEvent);
    }
}

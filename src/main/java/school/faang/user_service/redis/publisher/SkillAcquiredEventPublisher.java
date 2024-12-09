package school.faang.user_service.redis.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.redis.event.SkillAcquiredEvent;

@Component
@RequiredArgsConstructor
public class SkillAcquiredEventPublisher {

    @Value("${spring.data.redis.channel.skill}")
    private String skillAcquiredChannel;

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(SkillAcquiredEvent event) {
        redisTemplate.convertAndSend(skillAcquiredChannel, event);
    }
}

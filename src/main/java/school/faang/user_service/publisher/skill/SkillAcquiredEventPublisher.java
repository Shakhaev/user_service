package school.faang.user_service.publisher.skill;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.publisher.AbstractEventPublisher;
import school.faang.user_service.publisher.MessagePublisher;

@Slf4j
@Component
public class SkillAcquiredEventPublisher extends AbstractEventPublisher implements MessagePublisher {

    public SkillAcquiredEventPublisher(ObjectMapper objectMapper,
                                       RedisTemplate<String, Object> redisTemplate,
                                       ChannelTopic skillAcquireTopic) {
        super(objectMapper, redisTemplate, skillAcquireTopic);
    }

    @Override
    public void publish(Object object) {
        convertAndSend(object);
    }
}

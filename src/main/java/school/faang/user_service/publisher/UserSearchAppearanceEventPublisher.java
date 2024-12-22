package school.faang.user_service.publisher;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.events.UserSearchAppearanceEvent;

@Component
public class UserSearchAppearanceEventPublisher extends AbstractEventPublisher<UserSearchAppearanceEvent> {

    public UserSearchAppearanceEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic userSearchAppearanceTopic) {
        super(redisTemplate, userSearchAppearanceTopic);
    }

    @Override
    public Class<UserSearchAppearanceEvent> getInstance() {
        return UserSearchAppearanceEvent.class;
    }
}

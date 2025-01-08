package school.faang.user_service.publisher;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.events.RecommendationRequestedEvent;

@Component
public class RecommendationRequestedEventPublisher extends AbstractEventPublisher<RecommendationRequestedEvent> {
    public RecommendationRequestedEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic recommendationRequestedTopic) {
        super(redisTemplate, recommendationRequestedTopic);
    }

    @Override
    public Class<RecommendationRequestedEvent> getInstance() {
        return RecommendationRequestedEvent.class;
    }
}

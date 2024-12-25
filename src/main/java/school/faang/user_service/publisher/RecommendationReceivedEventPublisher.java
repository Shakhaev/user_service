package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.events.RecommendationReceivedEvent;

@Component
public class RecommendationReceivedEventPublisher extends AbstractEventPublisher<RecommendationReceivedEvent> {

    public RecommendationReceivedEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic recommendationReceivedTopic) {
        super(redisTemplate, recommendationReceivedTopic);
    }

    @Override
    public Class<RecommendationReceivedEvent> getInstance() {
        return RecommendationReceivedEvent.class;
    }
}

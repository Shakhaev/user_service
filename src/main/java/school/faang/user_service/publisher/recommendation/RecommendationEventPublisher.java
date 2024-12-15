package school.faang.user_service.publisher.recommendation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationEvent;
import school.faang.user_service.publisher.MessagePublisher;
import school.faang.user_service.service.recommendation.RecommendationService;

@Component
@Slf4j
@RequiredArgsConstructor
public class RecommendationEventPublisher implements MessagePublisher<RecommendationEvent> {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.recommendation_topic.name}")
    private String channel;

    @Override
    public void publish(RecommendationEvent event) {
            redisTemplate.convertAndSend(channel, event);
    }
}

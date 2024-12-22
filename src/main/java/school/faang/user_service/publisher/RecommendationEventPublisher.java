package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.RecommendationEvent;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationEventPublisher {
    private final RedisTemplate<String,Object> redisTemplate;


    private final ChannelTopic recommendationEventTopic;

    public void publishRecommendationEvent(RecommendationEvent event){
        log.info("Publishing RecomendationEvent: {}",event);
        try{
            redisTemplate.convertAndSend(recommendationEventTopic.getTopic(),event);
        }catch(RuntimeException e){
            log.error("Failed to publish RecommendationEvent: {}",e.getMessage());
        }
    }
}

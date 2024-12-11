package school.faang.user_service.publisher.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.SearchAppearanceEvent;
import school.faang.user_service.publisher.EventPublisherAbstract;

@Component
@Slf4j
public class SearchAppearanceEventPublisher extends EventPublisherAbstract<SearchAppearanceEvent> {
    @Value("${spring.data.redis.channels.search-appearance-channel}")
    private String topicSearchAppearanceEvent;

    public SearchAppearanceEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    public void publish(SearchAppearanceEvent event) {
        handleEvent(event, topicSearchAppearanceEvent);
    }
}
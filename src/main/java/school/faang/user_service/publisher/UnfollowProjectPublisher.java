package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.FollowerProjectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnfollowProjectPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publishUnfollowEvent(FollowerProjectEvent followerProjectEvent) {
        if (followerProjectEvent == null) {
            log.error("ProjectFollowerEvent равен null. Невозможно опубликовать событие.");
            throw new IllegalArgumentException("ProjectFollowerEvent не может быть null");
        }

        try {
            String serializedEvent = objectMapper.writeValueAsString(followerProjectEvent);
            redisTemplate.convertAndSend("unfollow_project_channel", serializedEvent);
            log.info("Опубликовано событие для Пользователя: {}",
                followerProjectEvent.ownerId());
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации события для Пользователя: {}",
                followerProjectEvent.ownerId(), e);
            throw new RuntimeException("Ошибка сериализации события ProjectFollowerEvent", e);
        }
    }
}

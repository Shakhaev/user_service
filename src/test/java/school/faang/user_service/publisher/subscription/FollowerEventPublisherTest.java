package school.faang.user_service.publisher.subscription;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.subscription.FollowerEvent;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FollowerEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private FollowerEventPublisher publisher;
    @Value("${spring.data.redis.channels.follower-event-channel.name}")
    private String followersChannel;

    @Test
    void testSuccessfulPublish() throws JsonProcessingException {
        FollowerEvent event = prepareEvent();
        when(objectMapper.writeValueAsString(event)).thenReturn("some_json");

        publisher.publish(event);

        verify(redisTemplate).convertAndSend(followersChannel, "some_json");
    }

    @Test
    void testPublishWithJsonProcessingException() throws JsonProcessingException {
        FollowerEvent event = prepareEvent();
        when(objectMapper.writeValueAsString(event)).thenThrow(JsonProcessingException.class);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> publisher.publish(event));

        assertEquals(RuntimeException.class, exception.getClass());
    }

    private FollowerEvent prepareEvent() {
        return FollowerEvent.builder()
                .followerUserId(1)
                .targetUserId(2L)
                .createdAt(LocalDateTime.now())
                .build();
    }
}

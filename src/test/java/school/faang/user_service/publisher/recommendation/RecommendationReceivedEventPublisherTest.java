package school.faang.user_service.publisher.recommendation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.recommendation.RecommendationReceivedEvent;
import school.faang.user_service.publisher.RecommendationReceivedEventPublisher;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationReceivedEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private RecommendationReceivedEventPublisher publisher;
    @Value("${spring.data.redis.channels.recommendation-received-channel.name}")
    private String recommendationChannel;

    @Test
    public void testSuccessfulPublish() throws JsonProcessingException {
        RecommendationReceivedEvent event = prepareEvent();
        when(objectMapper.writeValueAsString(event)).thenReturn("some_json");

        publisher.publish(event);

        verify(redisTemplate).convertAndSend(recommendationChannel, "some_json");
    }

    @Test
    public void testPublishWithJsonProcessingException() throws JsonProcessingException {
        RecommendationReceivedEvent event = prepareEvent();
        when(objectMapper.writeValueAsString(event)).thenThrow(JsonProcessingException.class);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> publisher.publish(event));

        assertEquals(RuntimeException.class, exception.getClass());
    }

    private RecommendationReceivedEvent prepareEvent() {
        return new RecommendationReceivedEvent(1L, 2L, 3L, "Content", LocalDateTime.now());
    }
}

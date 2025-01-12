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
import school.faang.user_service.dto.recommendation.RecommendationRequestedEvent;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestedEventPublisherTest {
    @Value("${spring.data.redis.channels.recommendation-requested-channel.name}")
    private String channel;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private RecommendationRequestedEventPublisher requestedEventPublisher;

    @Test
    void testSendMethodIsCalled() throws JsonProcessingException {
        String json = "json";
        RecommendationRequestedEvent requestedEvent = new RecommendationRequestedEvent();
        when(objectMapper.writeValueAsString(requestedEvent)).thenReturn(json);

        requestedEventPublisher.publish(requestedEvent);

        verify(objectMapper).writeValueAsString(requestedEvent);
        verify(redisTemplate).convertAndSend(channel, json);
    }

    @Test
    void testJsonProcessingExceptionGetWrappedAsRuntimeException() throws JsonProcessingException {
        RecommendationRequestedEvent requestedEvent = new RecommendationRequestedEvent();
        when(objectMapper.writeValueAsString(requestedEvent)).thenThrow(mock(JsonProcessingException.class));

        assertThrows(RuntimeException.class, () -> requestedEventPublisher.publish(requestedEvent));
    }
}
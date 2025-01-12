package school.faang.user_service.publisher.premium;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.premium.PremiumBoughtEvent;

import java.time.LocalDateTime;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumBoughtEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private PremiumBoughtEventPublisher premiumBoughtEventPublisher;
    @Value("${spring.data.redis.channels.premium-bought-channel.name}")
    private String premiumBoughtChannel;

    @Test
    void testSuccessfulPublish() throws JsonProcessingException {
        PremiumBoughtEvent event = setEvent();
        when(objectMapper.writeValueAsString(event)).thenReturn("some_json");
        premiumBoughtEventPublisher.publish(event);
        verify(redisTemplate).convertAndSend(premiumBoughtChannel, "some_json");
    }

    @Test
    void testPublishWithJsonProcessingException() throws JsonProcessingException {
        PremiumBoughtEvent event = setEvent();
        when(objectMapper.writeValueAsString(event)).thenThrow(JsonProcessingException.class);
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> premiumBoughtEventPublisher.publish(event));
        Assertions.assertEquals(IllegalStateException.class, exception.getClass());
    }

    private PremiumBoughtEvent setEvent() {
        return new PremiumBoughtEvent(1L, 1L, "Basic", LocalDateTime.now(), LocalDateTime.now());
    }
}
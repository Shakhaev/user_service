package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.event.PremiumBoughtEvent;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PremiumBoughtEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic buyPremiumTopic;
    @InjectMocks
    private PremiumBoughtEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        when(buyPremiumTopic.getTopic()).thenReturn("buy_premium_topic");
    }

    @Test
    void publishPremiumBoughtEvent_Positive() {
        PremiumBoughtEvent event = new PremiumBoughtEvent(123L, 29.99, 7, null);
        eventPublisher.publishPremiumBoughtEvent(event);
        verify(redisTemplate, times(1))
                .convertAndSend(eq("buy_premium_topic"), eq(event));
    }

    @Test
    void publishPremiumBoughtEvent_Negative_ExceptionThrow() {
        PremiumBoughtEvent event = new PremiumBoughtEvent(123L, 29.99, 7, null);
        doThrow(new RuntimeException("Redis error")).when(redisTemplate)
                .convertAndSend(anyString(), any());
        eventPublisher.publishPremiumBoughtEvent(event);
        verify(redisTemplate, times(1)).convertAndSend(eq("buy_premium_topic"), eq(event));
    }
}

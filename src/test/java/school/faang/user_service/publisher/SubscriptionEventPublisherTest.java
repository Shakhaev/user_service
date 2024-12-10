package school.faang.user_service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.event.SubscriptionEvent;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedisProperties redisProperties;

    @InjectMocks
    private SubscriptionEventPublisher subscriptionEventPublisher;

    @Test
    void testPublish() {
        SubscriptionEvent event = new SubscriptionEvent(1L, 2L, LocalDateTime.now());
        String subscriptionChannel = "subscription_event_channel";
        RedisProperties.Channel channel = new RedisProperties.Channel();
        channel.setSubscriptionChannel(subscriptionChannel);

        when(redisProperties.getChannel()).thenReturn(channel);

        subscriptionEventPublisher.publish(event);

        verify(redisTemplate, times(1)).convertAndSend(subscriptionChannel, event);
    }
}
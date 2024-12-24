package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.event.MentorshipAcceptedEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipAcceptedEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;

    private RedisProperties redisProperties;
    private MentorshipAcceptedEvent event;
    private String channel;


    @BeforeEach
    void setUp() {
        redisProperties = TestRedisPropertiesFactory.createDefaultRedisProperties();
        mentorshipAcceptedEventPublisher = new MentorshipAcceptedEventPublisher(redisTemplate,null, redisProperties);
        event = new MentorshipAcceptedEvent(1L, "Java", 3L, "John", 2L, "Mark");
        channel = redisProperties.channel().mentorshipAcceptedChannel();
    }


    @Test
    void testPublishSuccess() {

        mentorshipAcceptedEventPublisher.publish(event);

        verify(redisTemplate, times(1)).convertAndSend(channel, event);
    }

    @Test
    void testPublishFailure() {
        doThrow(new RuntimeException("Redis is down")).when(redisTemplate).convertAndSend(channel, event);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> mentorshipAcceptedEventPublisher.publish(event));
        assertEquals("Redis is down", exception.getMessage());

        verify(redisTemplate, times(1)).convertAndSend(channel, event);
    }
}
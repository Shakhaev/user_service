package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.RedisProperties;
import school.faang.user_service.dto.MentorshipRequestEvent;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestedEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedisProperties redisProperties;

    @InjectMocks
    private MentorshipRequestedEventPublisher publisher;

    private RedisProperties.Channel channel;
    private MentorshipRequestEvent event;


    @BeforeEach
    void setUp() {
        channel = new RedisProperties.Channel();
        channel.setMentorship_request("mentorship_request_channel");
        when(redisProperties.getChannel()).thenReturn(channel);
        event = new MentorshipRequestEvent();
    }

    @Test
    void testPublishSuccess() {
        MentorshipRequestEvent event = new MentorshipRequestEvent();
        when(redisTemplate.convertAndSend(eq("mentorship_request_channel"), eq(event))).thenReturn(null);

        CompletableFuture<Void> result = publisher.publish(event);

        assertTrue(result.isDone());
        assertFalse(result.isCompletedExceptionally());
        verify(redisTemplate, times(1)).convertAndSend(eq("mentorship_request_channel"), eq(event));
    }

    @Test
    void testPublishRedisException() {
        MentorshipRequestEvent event = new MentorshipRequestEvent();
        doThrow(new RedisException("Redis error")).when(redisTemplate).convertAndSend(eq("mentorship_request_channel"), eq(event));

        CompletableFuture<Void> result = publisher.publish(event);

        assertTrue(result.isCompletedExceptionally());
        verify(redisTemplate, times(1)).convertAndSend(eq("mentorship_request_channel"), eq(event));
    }

    @Test
    void testPublishUnexpectedException() {
        MentorshipRequestEvent event = new MentorshipRequestEvent();
        doThrow(new RuntimeException("Unexpected error")).when(redisTemplate).convertAndSend(eq("mentorship_request_channel"), eq(event));

        CompletableFuture<Void> result = publisher.publish(event);

        assertTrue(result.isCompletedExceptionally());
        verify(redisTemplate, times(1)).convertAndSend(eq("mentorship_request_channel"), eq(event));
    }
}

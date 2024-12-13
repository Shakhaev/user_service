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
    @Mock
    private RedisProperties redisProperties;
    @InjectMocks
    private MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;

    private String channelName;
    private MentorshipAcceptedEvent event;
    private RedisProperties.Channel channel;

    @BeforeEach
    void setUp() {
        channelName = "mentorship_accepted_channel";
        event = new MentorshipAcceptedEvent(1L, "Java", 3L, "John", 2L);
        channel = new RedisProperties.Channel();
        channel.setMentorship_acceptedChannel(channelName);
    }

    @Test
    void testPublishSuccess() {
        when(redisProperties.getChannel()).thenReturn(channel);

        mentorshipAcceptedEventPublisher.publish(event);

        verify(redisTemplate, times(1)).convertAndSend(channelName, event);
    }

    @Test
    void testPublishFailure() {
        when(redisProperties.getChannel()).thenReturn(channel);

        doThrow(new RuntimeException("Redis is down")).when(redisTemplate).convertAndSend(channelName, event);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> mentorshipAcceptedEventPublisher.publish(event));
        assertEquals("Redis is down", exception.getMessage());

        verify(redisTemplate, times(1)).convertAndSend(channelName, event);
    }
}
package school.faang.user_service.publisher.mentorship;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.mentorshiprequest.MentorshipAcceptedEvent;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipAcceptedEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private MentorshipAcceptedEventPublisher publisher;
    @Value("${spring.data.redis.channels.mentorship_accepted}")
    private String channel;

    @Test
    void testSuccessfulPublish() throws JsonProcessingException {
        MentorshipAcceptedEvent event = provideEvent();
        when(objectMapper.writeValueAsString(event)).thenReturn("{}");

        publisher.publish(event);

        verify(redisTemplate).convertAndSend(channel, "{}");
    }

    @Test
    void testPublishWithJsonProcessingException() throws JsonProcessingException {
        MentorshipAcceptedEvent event = provideEvent();
        when(objectMapper.writeValueAsString(event)).thenThrow(JsonProcessingException.class);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> publisher.publish(event));

        assertEquals(RuntimeException.class, exception.getClass());
    }

    private MentorshipAcceptedEvent provideEvent() {
        return new MentorshipAcceptedEvent(1L, 2L, 3L, LocalDateTime.now());
    }
}

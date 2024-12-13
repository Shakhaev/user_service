package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.event.MentorshipAcceptedEvent;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipAcceptedEventPublisherTest {

    @InjectMocks
    private MentorshipAcceptedEventPublisher eventPublisher;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ChannelTopic mentorshipAcceptedTopic;

    @Test
    public void testPublisher() throws JsonProcessingException {
        MentorshipAcceptedEvent event = new MentorshipAcceptedEvent();

        when(mentorshipAcceptedTopic.getTopic()).thenReturn("topic");
        when(objectMapper.writeValueAsString(event)).thenReturn("JSON");

        eventPublisher.publish(event);

        verify(redisTemplate).convertAndSend("topic", "JSON");
    }
}

package school.faang.user_service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.events.MentorshipAcceptedEvent;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MentorshipAcceptedEventPublisherTest {

    @InjectMocks
    private MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic channelTopic;
    private String topic = "topic";

    @Test
    void testSuccessfulSendingMessage() {
        MentorshipAcceptedEvent mentorshipStartEvent = new MentorshipAcceptedEvent(1,2, LocalDateTime.now(),3);

        Mockito.when(channelTopic.getTopic()).thenReturn(topic);

        mentorshipAcceptedEventPublisher.publish(mentorshipStartEvent);

        Mockito.verify(redisTemplate).convertAndSend(topic, mentorshipStartEvent);
    }


}

package school.faang.user_service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.MentorshipOfferedEvent;
import school.faang.user_service.publisher.mentorshipoffered.MentorshipOfferedPublisher;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipOfferedMessagePublisherTest {
    private static final String TOPIC_NAME = "user-ban-topic";
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic userBanTopic;
    @InjectMocks
    private MentorshipOfferedPublisher mentorshipOfferedPublisher;

    @Test
    void publishSuccessTest() {
        MentorshipOfferedEvent mentorshipOfferedEvent = new MentorshipOfferedEvent(1L, 2L, 3L);
        when(userBanTopic.getTopic()).thenReturn(TOPIC_NAME);
        assertDoesNotThrow(() -> mentorshipOfferedPublisher.publish(mentorshipOfferedEvent));
        verify(redisTemplate).convertAndSend(TOPIC_NAME, mentorshipOfferedEvent);
    }
}
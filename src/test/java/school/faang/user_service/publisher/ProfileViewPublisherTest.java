package school.faang.user_service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.ProfileViewEvent;
import school.faang.user_service.publisher.mentorshipoffered.ProfileViewEventPublisher;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfileViewPublisherTest {
    private static final String TOPIC_NAME = "profile-view-topic";
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic profileViewTopic;
    @InjectMocks
    private ProfileViewEventPublisher profileViewEventPublisher;

    @Test
    void publishSuccessTest() {
        ProfileViewEvent profileViewEvent = new ProfileViewEvent(1L, 2L, LocalDateTime.now());
        when(profileViewTopic.getTopic()).thenReturn(TOPIC_NAME);
        assertDoesNotThrow(() -> profileViewEventPublisher.publish(profileViewEvent));
        verify(redisTemplate).convertAndSend(TOPIC_NAME, profileViewEvent);
    }
}
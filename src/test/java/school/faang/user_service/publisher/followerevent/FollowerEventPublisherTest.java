package school.faang.user_service.publisher.followerevent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.FollowerEvent;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowerEventPublisherTest {
    private static final String TOPIC_NAME = "follower-test";
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic followerTopic;
    @InjectMocks
    private FollowerEventPublisher followerEventPublisher;

    @Test
    void publishSuccessTest() {
        FollowerEvent followerEvent = FollowerEvent.builder()
                .followerId(1L)
                .followeeId(2L)
                .receivedAt(LocalDateTime.now())
                .build();

        when(followerTopic.getTopic()).thenReturn(TOPIC_NAME);
        assertDoesNotThrow(() -> followerEventPublisher.publish(followerEvent));
        verify(redisTemplate).convertAndSend(TOPIC_NAME, followerEvent);
    }
}
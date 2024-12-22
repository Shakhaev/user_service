package school.faang.user_service.publisher;

import io.lettuce.core.RedisConnectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.event.GoalCompletedEvent;
import school.faang.user_service.exception.RedisPublishingException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalCompletedEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private GoalCompletedEventPublisher goalCompletedEventPublisher;

    private final String topic = "test-topic";

    @BeforeEach
    void setUp() {
        RedisProperties.Channel redisChannel = new RedisProperties.Channel(
                "subscription_channel",
                "recommendation_channel",
                "goal_channel",
                "user_ban_channel",
                "mentorship_request_channel"
        );

        RedisProperties redisProperties = new RedisProperties("localhost", 6379, redisChannel);

        goalCompletedEventPublisher = new GoalCompletedEventPublisher(redisTemplate, redisProperties);
    }


    @Test
    void testPublishSuccess() {
        GoalCompletedEvent event = setUpGoalCompletedEvent();

        when(redisTemplate.convertAndSend("goal_channel", event)).thenReturn(1L);

        goalCompletedEventPublisher.publish(event);

        verify(redisTemplate, times(1)).convertAndSend("goal_channel", event);
    }

    @Test
    void testPublishRedisConnectionException() {
        when(redisTemplate.convertAndSend("goal_channel", setUpGoalCompletedEvent()))
                .thenThrow(RedisConnectionException.class);

        assertThrows(RedisConnectionException.class, () ->
                goalCompletedEventPublisher.publish(setUpGoalCompletedEvent()));

        verify(redisTemplate, times(1))
                .convertAndSend("goal_channel", setUpGoalCompletedEvent());
    }

    @Test
    void testPublishUnexpectedException() {
        when(redisTemplate.convertAndSend("goal_channel", setUpGoalCompletedEvent()))
                .thenThrow(new RuntimeException("Unexpected error"));

        RedisPublishingException exception = assertThrows(RedisPublishingException.class, () ->
                goalCompletedEventPublisher.publish(setUpGoalCompletedEvent()));

        verify(redisTemplate, times(1))
                .convertAndSend("goal_channel", setUpGoalCompletedEvent());
        assertEquals("Unexpected error while publishing event to Redis", exception.getMessage());
    }

    private GoalCompletedEvent setUpGoalCompletedEvent() {
        return new GoalCompletedEvent(1L, 2L,
                LocalDateTime.of(2021, 1, 1, 0, 0));
    }
}

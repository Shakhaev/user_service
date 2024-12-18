package school.faang.user_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import school.faang.user_service.entity.OutboxEvent;
import school.faang.user_service.event.GoalCompletedEvent;
import school.faang.user_service.exception.OutboxProcessingException;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.repository.OutboxEventRepository;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OutboxProcessorTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private GoalCompletedEventPublisher goalCompletedEventPublisher;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OutboxProcessor outboxProcessor;

    private final long userId = 1L;
    private final long goalId = 1L;
    private final String eventPayload = "{\"userId\":1,\"goalId\":1,\"completedAt\":\"2023-04-06T12:00:00\"}";

    @Test
    void processOutboxEventsSuccess() throws Exception {
        setupOutboxEvent().setEventPayload(eventPayload);
        when(outboxEventRepository.findAllByProcessedFalse())
                .thenReturn(Collections.singletonList(setupOutboxEvent()));
        when(objectMapper.readValue(anyString(), eq(GoalCompletedEvent.class)))
                .thenReturn(new GoalCompletedEvent(1L, 1L, LocalDateTime.now()));

        outboxProcessor.processOutboxEvents();

        Mockito.verify(goalCompletedEventPublisher, times(1)).publish(any(GoalCompletedEvent.class));
        Mockito.verify(outboxEventRepository, times(1)).save(any(OutboxEvent.class));
    }

    @Test
    void testSaveEventSuccess() {
        when(outboxEventRepository.findAllByProcessedFalse()).thenReturn(Collections.singletonList(new OutboxEvent()));

        outboxProcessor.saveEvent(setupGoalCompletedEvent());

        verify(outboxEventRepository, times(1)).save(any(OutboxEvent.class));
        assertEquals(1, outboxEventRepository.findAllByProcessedFalse().size());
    }

    @Test
    void testProcessEventSerializationException() throws Exception {
        when(outboxEventRepository.findAllByProcessedFalse()).thenReturn(Collections.singletonList(setupOutboxEvent()));
        when(objectMapper.readValue(anyString(), eq(GoalCompletedEvent.class)))
                .thenThrow(JsonProcessingException.class);

        assertThrows(OutboxProcessingException.class, () -> outboxProcessor.processOutboxEvents(),
                "Error processing outbox event:");

        verify(goalCompletedEventPublisher, never()).publish(any(GoalCompletedEvent.class));
        verify(outboxEventRepository, never()).save(any(OutboxEvent.class));
    }

    @Test
    void testProcessEventDataAccessException() throws Exception {
        when(outboxEventRepository.findAllByProcessedFalse()).thenReturn(Collections.singletonList(setupOutboxEvent()));
        when(objectMapper.readValue(anyString(), eq(GoalCompletedEvent.class)))
                .thenThrow(new DataAccessException("Error processing outbox event:") {
                });

        assertThrows(OutboxProcessingException.class, () -> outboxProcessor.processOutboxEvents(),
                "Error processing outbox event:");

        verify(goalCompletedEventPublisher, never()).publish(any(GoalCompletedEvent.class));
        verify(outboxEventRepository, never()).save(any(OutboxEvent.class));
    }

    @Test
    void testSaveEventJsonProcessingException() throws Exception {
        when(objectMapper.writeValueAsString(any(GoalCompletedEvent.class))).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> outboxProcessor.saveEvent(setupGoalCompletedEvent()));

        verify(outboxEventRepository, never()).save(any(OutboxEvent.class));
        assertEquals(0, outboxEventRepository.findAllByProcessedFalse().size());
    }


    private GoalCompletedEvent setupGoalCompletedEvent() {
        return new GoalCompletedEvent(userId, goalId, LocalDateTime.now());
    }

    private OutboxEvent setupOutboxEvent() {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventPayload(eventPayload);
        return outboxEvent;
    }
}

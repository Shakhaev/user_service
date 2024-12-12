package school.faang.user_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.SerializationException;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.OutboxEvent;
import school.faang.user_service.event.GoalCompletedEvent;
import school.faang.user_service.exception.OutboxProcessingException;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.repository.OutboxEventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxProcessor {
    private final OutboxEventRepository outboxEventRepository;
    private final GoalCompletedEventPublisher goalCompletedEventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    @Scheduled(fixedRate = 5000)
    public void processOutboxEvents() {
        List<OutboxEvent> events = outboxEventRepository.findAllByProcessedFalse();
        events.forEach(this::processEvent);
    }

    @Transactional
    public void saveEvent(GoalCompletedEvent event) {
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .eventType(event.getClass().getSimpleName())
                .eventPayload(serializeEvent(event))
                .createdAt(event.completedAt())
                .processed(false)
                .build();
        outboxEventRepository.save(outboxEvent);
    }

    @Retryable(
            value = {OutboxProcessingException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2))
    private void processEvent(OutboxEvent event) {
        try {
            GoalCompletedEvent goalCompletedEvent = deserializeEvent(event.getEventPayload());
            goalCompletedEventPublisher.publish(goalCompletedEvent);
            event.setProcessed(true);
            outboxEventRepository.save(event);
        } catch (DataAccessException | SerializationException e) {
            log.error("Specific error processing outbox event", e);
            throw new OutboxProcessingException("Error processing outbox event:", e);
        } catch (Exception e) {
            log.error("Unexpected error processing outbox event", e);
            throw new OutboxProcessingException("Unexpected error processing outbox event:", e);
        }
    }

    private String serializeEvent(GoalCompletedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Error while serializing GoalCompletedEvent", e);
            throw new RuntimeException(e);
        }
    }

    private GoalCompletedEvent deserializeEvent(String eventPayload) {
        try {
            return objectMapper.readValue(eventPayload, GoalCompletedEvent.class);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing event", e);
            throw new RuntimeException("Error deserializing event", e);
        }
    }
}

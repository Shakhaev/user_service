package school.faang.user_service.listener;

import jakarta.persistence.PostPersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.OutboxEvent;
import school.faang.user_service.outbox.OutboxEventProcessor;

@Component
public class OutboxEventListener {
    private static OutboxEventProcessor outboxProcessor;

    @Autowired
    public void setOutboxProcessor(OutboxEventProcessor outboxProcessor) {
        OutboxEventListener.outboxProcessor = outboxProcessor;
    }

    @PostPersist
    public void onPostPersist(OutboxEvent event) {
        outboxProcessor.triggerProcessing();
    }
}

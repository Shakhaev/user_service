package school.faang.user_service.scheduler.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventStartEvent;
import school.faang.user_service.dto.event.EventStartReminderEvent;
import school.faang.user_service.publisher.event.EventStartEventPublisher;
import school.faang.user_service.publisher.event.EventStartReminderEventPublisher;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventNotificationJob implements Job {
    private final EventStartEventPublisher startEventPublisher;
    private final EventStartReminderEventPublisher reminderEventPublisher;
    private final ObjectMapper objectMapper;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        Long eventId = data.getLong("eventId");
        String attendeesJson = data.getString("attendeesIds");
        String notificationType = data.getString("notificationType");

        List<Long> attendeesIds = deserializeAttendees(attendeesJson);

        log.info("Executing notification job for eventId={}, notificationType={}", eventId, notificationType);

        if ("start".equals(notificationType)) {
            EventStartEvent eventStartEvent = EventStartEvent.builder()
                    .eventId(eventId)
                    .attendeesIds(attendeesIds)
                    .build();
            startEventPublisher.publish(eventStartEvent);
        } else {
            EventStartReminderEvent reminderEvent = EventStartReminderEvent.builder()
                    .eventId(eventId)
                    .attendeesIds(attendeesIds)
                    .reminderType(notificationType)
                    .build();
            reminderEventPublisher.publish(reminderEvent);
        }
    }

    private List<Long> deserializeAttendees(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize attendeesIds", e);
        }
    }
}

package school.faang.user_service.scheduler.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventStartNotificationScheduler {
    private final Scheduler scheduler;
    private final ObjectMapper objectMapper;

    public void scheduleEventStartNotification(Long eventId, List<Long> attendeesIds, ZonedDateTime startTime) {
        scheduleNotificationJob(eventId, attendeesIds, startTime.minusDays(1), "1day");
        scheduleNotificationJob(eventId, attendeesIds, startTime.minusHours(5), "5hours");
        scheduleNotificationJob(eventId, attendeesIds, startTime.minusHours(1), "1hour");
        scheduleNotificationJob(eventId, attendeesIds, startTime.minusMinutes(10), "10minutes");
        scheduleNotificationJob(eventId, attendeesIds, startTime, "start");
    }

    private void scheduleNotificationJob(Long eventId, List<Long> attendeesIds, ZonedDateTime time, String notificationType) {
        JobDetail jobDetail = buildJobDetail(eventId, attendeesIds, notificationType);
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("eventNotificationTrigger_" + eventId + "_" + notificationType, "eventNotifications")
                .startAt(Date.from(time.toInstant()))
                .build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to schedule event notification job", e);
        }
    }

    private JobDetail buildJobDetail(Long eventId, List<Long> attendeesIds, String notificationType) {
        String attendeesJson = serializeAttendees(attendeesIds);

        return JobBuilder.newJob(EventNotificationJob.class)
                .withIdentity("eventNotificationJob_" + eventId + "_" + notificationType, "eventNotifications")
                .usingJobData("eventId", eventId)
                .usingJobData("attendeesIds", attendeesJson)
                .usingJobData("notificationType", notificationType)
                .build();
    }

    private String serializeAttendees(List<Long> attendeesIds) {
        try {
            return objectMapper.writeValueAsString(attendeesIds);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize attendeesIds", e);
        }
    }
}

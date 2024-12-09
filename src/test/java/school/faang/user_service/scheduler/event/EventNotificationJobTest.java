package school.faang.user_service.scheduler.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.event.EventStartEvent;
import school.faang.user_service.dto.event.EventStartReminderEvent;
import school.faang.user_service.publisher.event.EventStartEventPublisher;
import school.faang.user_service.publisher.event.EventStartReminderEventPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventNotificationJobTest {

    private static final Long START_EVENT_ID = 100L;
    private static final Long REMINDER_EVENT_ID = 200L;
    private static final String REMINDER_TYPE = "1day";

    @Mock
    private EventStartEventPublisher startEventPublisher;

    @Mock
    private EventStartReminderEventPublisher reminderEventPublisher;

    @InjectMocks
    private EventNotificationJob eventNotificationJob;

    private JobExecutionContext context;
    private JobDataMap jobDataMap;

    @BeforeEach
    void setUp() {
        context = mock(JobExecutionContext.class);
        JobDetail jobDetail = mock(JobDetail.class);
        jobDataMap = new JobDataMap();

        when(context.getJobDetail()).thenReturn(jobDetail);
        when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);

        ReflectionTestUtils.setField(eventNotificationJob, "objectMapper", new ObjectMapper());
    }

    @Test
    @DisplayName("Should publish EventStartEvent when notificationType is 'start'")
    void testExecuteStartEvent() throws Exception {
        jobDataMap.put("eventId", START_EVENT_ID);
        jobDataMap.put("attendeesIds", "[1,2,3]");
        jobDataMap.put("notificationType", "start");

        eventNotificationJob.execute(context);

        verify(startEventPublisher, times(1)).publish(any(EventStartEvent.class));
        verify(reminderEventPublisher, never()).publish(any(EventStartReminderEvent.class));
    }

    @Test
    @DisplayName("Should publish EventStartReminderEvent when notificationType is '1day'")
    void testExecuteReminderEvent() throws Exception {
        jobDataMap.put("eventId", REMINDER_EVENT_ID);
        jobDataMap.put("attendeesIds", "[10,20]");
        jobDataMap.put("notificationType", REMINDER_TYPE);

        eventNotificationJob.execute(context);

        verify(reminderEventPublisher, times(1)).publish(any(EventStartReminderEvent.class));
        verify(startEventPublisher, never()).publish(any(EventStartEvent.class));
    }
}

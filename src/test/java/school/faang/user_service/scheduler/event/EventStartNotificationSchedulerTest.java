package school.faang.user_service.scheduler.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventStartNotificationSchedulerTest {

    private static final Long EVENT_ID = 100L;
    private static final List<Long> ATTENDEES = List.of(1L, 2L, 3L);
    private static final ZonedDateTime START_TIME = ZonedDateTime.now().plusDays(2);
    private static final String ATTENDEES_JSON = "[1,2,3]";

    @Mock
    private Scheduler scheduler;

    @InjectMocks
    private EventStartNotificationScheduler eventScheduler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(eventScheduler, "objectMapper", new ObjectMapper());
    }

    @Test
    @DisplayName("Should schedule 5 jobs for event notifications")
    void testScheduleEventStartNotification() throws Exception {
        eventScheduler.scheduleEventStartNotification(EVENT_ID, ATTENDEES, START_TIME);

        ArgumentCaptor<JobDetail> jobDetailCaptor = ArgumentCaptor.forClass(JobDetail.class);
        ArgumentCaptor<Trigger> triggerCaptor = ArgumentCaptor.forClass(Trigger.class);

        verify(scheduler, times(5)).scheduleJob(jobDetailCaptor.capture(), triggerCaptor.capture());

        List<JobDetail> jobDetails = jobDetailCaptor.getAllValues();
        List<Trigger> triggers = triggerCaptor.getAllValues();

        assertEquals(5, jobDetails.size(), "Должно быть запланировано 5 заданий");
        assertEquals(5, triggers.size(), "Должно быть запланировано 5 триггеров");

        List<String> expectedTypes = List.of("1day", "5hours", "1hour", "10minutes", "start");

        for (JobDetail jd : jobDetails) {
            JobDataMap map = jd.getJobDataMap();
            String notificationType = map.getString("notificationType");
            String attendeesIds = map.getString("attendeesIds");

            assertTrue(expectedTypes.contains(notificationType),
                    "Тип уведомления должен быть одним из ожидаемых");
            assertEquals(ATTENDEES_JSON, attendeesIds,
                    "Список участников должен совпадать с JSON-строкой");
        }
    }
}

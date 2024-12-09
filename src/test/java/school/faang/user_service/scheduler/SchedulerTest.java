package school.faang.user_service.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SchedulerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private Scheduler scheduler;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(scheduler, "batchSize", 3);
    }

    @Test
    public void testClearPastEventsPositive() throws InterruptedException {
        ReflectionTestUtils.setField(scheduler, "batchSize", 1);
        EventDto event1 = EventDto.builder().id(1L).build();
        EventDto event2 = EventDto.builder().id(2L).build();
        EventDto event3 = EventDto.builder().id(3L).build();

        List<EventDto> eventsToDelete = new ArrayList<>(List.of(event1, event2, event3));

        when(eventService.getPastEventsIds()).thenReturn(eventsToDelete);

        scheduler.clearEvents();

        Thread.sleep(1000);
        verify(eventService, times(3)).deleteEvent(anyLong());
    }

    @Test
    public void testClearEvents_NoPastEvents_ShouldLogNoEventsToDelete() {
        when(eventService.getPastEventsIds()).thenReturn(Collections.emptyList());

        scheduler.clearEvents();

        verify(eventService, never()).deleteEvent(anyLong());
    }
}

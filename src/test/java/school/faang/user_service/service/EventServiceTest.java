package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    public  void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void deactivateEventsByUser_updatesStatusAndDeletesEvents() {
        Long userId = 1L;

        Event event1 = new Event();
        event1.setStatus(EventStatus.COMPLETED);
        event1.setId(100L);

        Event event2 = new Event();
        event2.setStatus(EventStatus.COMPLETED);
        event2.setId(200L);

        List<Event> events = Arrays.asList(event1, event2);
        assertThat(events).hasSize(2);
        assertThat(events.get(0).getStatus()).isEqualTo(EventStatus.COMPLETED);
        assertThat(events.get(1).getStatus()).isEqualTo(EventStatus.COMPLETED);
        assertThat(events).extracting(Event::getId).containsOnly(100L, 200L);
    }

    @Test
    void deactivateEventsByUser_ShouldDeactivateAndDeleteEvents() {
        Long userId = 1L;

        Event event1 = new Event();
        event1.setId(1L);
        event1.setId(userId);
        event1.setStatus(EventStatus.IN_PROGRESS);

        Event event2 = new Event();
        event2.setId(2L);
        event2.setId(userId);
        event2.setStatus(EventStatus.IN_PROGRESS);

        List<Event> events = Arrays.asList(event1, event2);

        when(eventRepository.findAllByUserId(userId)).thenReturn(events);

        eventService.deactivateEventsByUser(userId);

        verify(eventRepository, times(2)).save(event1);
        verify(eventRepository, times(2)).delete(event1);

        verify(eventRepository, times(2)).save(event2);
        verify(eventRepository, times(2)).delete(event2);

        assert event1.getStatus() == EventStatus.COMPLETED;
        assert event2.getStatus() == EventStatus.COMPLETED;
    }
}

package school.faang.user_service.service.event;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;
    @InjectMocks
    private EventService eventService;

    @Test
    public void testCreateEvent() {
        EventDto eventDto = EventDto.builder()
                .title("Test Event")
                .ownerId(1L)
                .build();

        Event event = new Event();
        event.setTitle("Test Event");

        when(eventMapper.toEntity(any(EventDto.class))).thenReturn(event);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toDto(any(Event.class))).thenReturn(eventDto);

        EventDto createdEvent = eventService.create(eventDto);

        assertNotNull(createdEvent);
        assertEquals("Test Event", createdEvent.getTitle());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    public void testCreateEventWithoutSkills() {
        EventDto eventDto = EventDto.builder()
                .title("Test Event")
                .ownerId(1L)
                .build();

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    public void testGetEvent() {
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Test Event");

        EventDto eventDto = EventDto.builder()
                .id(eventId)
                .title("Test Event")
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        EventDto foundEvent = eventService.getEvent(eventId);

        assertNotNull(foundEvent);
        assertEquals(eventId, foundEvent.getId());
        assertEquals("Test Event", foundEvent.getTitle());
        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    public void testGetEventsByFilter() {
        long eventId = 1L;
        String eventTitle = "Test Event";

        Event event = new Event();
        event.setId(eventId);
        event.setTitle(eventTitle);

        List<Event> events = new ArrayList<>();
        events.add(event);

        EventFilterDto eventFilterDto = new EventFilterDto();
        eventFilterDto.setTitle(eventTitle);

        EventDto eventDto = EventDto.builder()
                .id(eventId)
                .title(eventTitle)
                .build();

        EventDto[] eventsDto = { eventDto };

        when(eventRepository.findAll()).thenReturn(events);

        EventDto[] foundEvents = eventService.getEventsByFilter(eventFilterDto);

        assertNotNull(foundEvents);
        assertEquals(eventsDto.length, foundEvents.length);
        assertEquals(eventsDto[0].getTitle(), foundEvents[0].getTitle());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteEvent() {
        Long eventId = 1L;

        doNothing().when(eventRepository).deleteById(eventId);

        eventService.deleteEvent(eventId);

        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    public void testUpdateEvent() {
        EventDto eventDto2 = EventDto.builder()
                .title("Test Event2")
                .ownerId(1L)
                .build();

        Event event = new Event();
        event.setTitle("Test Event2");

        when(eventMapper.toEntity(any(EventDto.class))).thenReturn(event);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toDto(any(Event.class))).thenReturn(eventDto2);

        EventDto updatedEvent = eventService.updateEvent(eventDto2);

        assertNotNull(updatedEvent);
        assertEquals("Test Event2", updatedEvent.getTitle());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    public void testGetOwnedEvents() {
        long userId = 1L;

        long eventId = 1L;
        String eventTitle = "Test Event";

        Event event = new Event();
        event.setId(eventId);
        event.setTitle(eventTitle);

        List<Event> events = new ArrayList<>();
        events.add(event);

        EventDto eventDto = EventDto.builder()
                .id(eventId)
                .title(eventTitle)
                .build();

        EventDto[] eventsDto = { eventDto };

        when(eventRepository.findAllByUserId(userId)).thenReturn(events);

        EventDto[] foundEvents = eventService.getOwnedEvents(userId);

        assertNotNull(foundEvents);
        assertEquals(eventsDto.length, foundEvents.length);
        assertEquals(eventsDto[0].getTitle(), foundEvents[0].getTitle());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    public void testGetParticipatedEvents() {
        long userId = 1L;

        long eventId = 1L;
        String eventTitle = "Test Event";

        Event event = new Event();
        event.setId(eventId);
        event.setTitle(eventTitle);

        List<Event> events = new ArrayList<>();
        events.add(event);

        EventDto eventDto = EventDto.builder()
                .id(eventId)
                .title(eventTitle)
                .build();

        EventDto[] eventsDto = { eventDto };

        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(events);

        EventDto[] foundEvents = eventService.getParticipatedEvents(userId);

        assertNotNull(foundEvents);
        assertEquals(eventsDto.length, foundEvents.length);
        assertEquals(eventsDto[0].getTitle(), foundEvents[0].getTitle());
        verify(eventRepository, times(1)).findAll();
    }
}

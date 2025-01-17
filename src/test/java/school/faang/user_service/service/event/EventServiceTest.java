package school.faang.user_service.service.event;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validation.event.EventValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private EventMapperImpl eventMapper;
    @Spy
    private EventValidator eventValidator;
    @InjectMocks
    private EventService eventService;

    @Test
    public void testCreateEvent() {
        Event event = new Event();
        event.setTitle("Test Event");

        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event createdEvent = eventService.create(event);

        assertNotNull(createdEvent);
        assertEquals("Test Event", createdEvent.getTitle());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    public void testCreateEventWithoutSkills() {
        Event event = new Event();
        event.setTitle("Test Event");

        assertThrows(DataValidationException.class, () -> eventService.create(event));
    }

    @Test
    public void testGetEvent() {
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Test Event");

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        Event foundEvent = eventService.getEvent(eventId);

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

        when(eventRepository.findAll()).thenReturn(events);

        List<Event> foundEvents = eventService.getEventsByFilter(eventFilterDto);

        assertNotNull(foundEvents);
        assertEquals(events.size(), foundEvents.size());
        assertEquals(events.get(0).getTitle(), foundEvents.get(0).getTitle());
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
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event updatedEvent = eventService.updateEvent(event);

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

        when(eventRepository.findAllByUserId(userId)).thenReturn(events);

        List<Event> foundEvents = eventService.getOwnedEvents(userId);

        assertNotNull(foundEvents);
        assertEquals(events.size(), foundEvents.size());
        assertEquals(events.get(0).getTitle(), foundEvents.get(0).getTitle());
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

        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(events);

        List<Event> foundEvents = eventService.getParticipatedEvents(userId);


        assertNotNull(foundEvents);
        assertEquals(events.size(), foundEvents.size());
        assertEquals(events.get(0).getTitle(), foundEvents.get(0).getTitle());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    public void testIsValid_StartTimeAfterEndTime() {
        EventDto eventDto = EventDto.builder()
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now())
                .build();

        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);

        assertFalse(eventValidator.isValid(eventDto, context));
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Start time bust be before end");
        verify(builder).addPropertyNode("startTime");
        verify(builder).addConstraintViolation();
    }
}

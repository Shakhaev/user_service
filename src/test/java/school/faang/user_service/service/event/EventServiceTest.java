package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Spy
    private EventMapper eventMapper;
    @Mock
    private List<EventFilter> eventFilters;
    @InjectMocks
    private EventService eventService;

    private Event event;
    private User owner;

    @BeforeEach
    void setUp() {
        Skill firstSkill = Skill.builder().title("Java").build();
        Skill secondSkill = Skill.builder().title("Spring").build();
        List<Skill> skills = List.of(firstSkill, secondSkill);
        owner = User.builder()
                .id(1L)
                .skills(skills)
                .build();
        event = Event.builder()
                .id(1L)
                .owner(owner)
                .relatedSkills(skills)
                .build();
    }

    @Test
    void testCreateEventValidSkills() {
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event createdEvent = eventService.create(event);

        assertNotNull(createdEvent);
        assertEquals(createdEvent.getOwner().getId(), owner.getId());
        assertEquals(createdEvent.getRelatedSkills().size(), owner.getSkills().size());
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void testCreateEventInvalidSkills() {
        Skill testSkill = Skill.builder().title("Python").build();
        List<Skill> modifiedList = new ArrayList<>(owner.getSkills());
        modifiedList.add(testSkill);
        event.setRelatedSkills(modifiedList);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> eventService.create(event));
        assertEquals(String.format(
                "User with id %d don't have all related skills to create event id %d",
                owner.getId(), event.getId()), exception.getMessage());
    }

    @Test
    void testGetEvent() {
        Long eventId = event.getId();
        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));

        Event foundEvent = eventService.getEvent(eventId);

        assertNotNull(foundEvent);
        assertEquals(1L, foundEvent.getId());
        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    void testGetEventNotFound() {
        Long eventId = event.getId();
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> eventService.getEvent(eventId));
        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    void testGetEventsByFilter() {
        EventFilters filters = EventFilters.builder().title("Test").build();
        when(eventRepository.findAll()).thenReturn(Collections.singletonList(event));
        when(eventFilters.stream()).thenReturn(Stream.of(mock(EventFilter.class)));

        List<Event> filteredEvents = eventService.getEventsByFilter(filters);

        assertNotNull(filteredEvents);
        assertEquals(1, filteredEvents.size());
    }

    @Test
    void testDeleteEvent() {
        doNothing().when(eventRepository).deleteById(1L);

        eventService.deleteEvent(1L);

        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateEvent() {
        Event eventToUpdate = Event.builder()
                .id(1L)
                .owner(owner)
                .build();
        eventToUpdate.setRelatedSkills(Collections.singletonList(
                Skill.builder().title("Java").build()));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        eventService.updateEvent(eventToUpdate);

        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testUpdateEventNotFound() {
        Long eventId = event.getId();
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventService.updateEvent(event));

        assertEquals(String.format("Event id %d not found", eventId), exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void testGetOwnedEvents() {
        when(eventRepository.findAllByUserId(owner.getId())).thenReturn(Collections.emptyList());

        eventService.getOwnedEvents(owner.getId());

        verify(eventRepository, times(1)).findAllByUserId(owner.getId());
    }

    @Test
    void testGetParticipatedEvents() {
        when(eventRepository.findParticipatedEventsByUserId(owner.getId())).thenReturn(Collections.emptyList());

        eventService.getParticipatedEvents(owner.getId());

        verify(eventRepository, times(1)).findParticipatedEventsByUserId(owner.getId());
    }
}

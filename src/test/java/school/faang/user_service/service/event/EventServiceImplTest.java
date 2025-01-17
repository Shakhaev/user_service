package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {
    @Mock
    private EventRepository eventRepository;
    @Spy
    private EventMapperImpl eventMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private EventServiceImpl eventService;
    @Captor
    private ArgumentCaptor<Event> captor;
    @Spy
    private final List<EventFilter> filters = TestData.createFilters();
    private final Event event = TestData.createEvent(1L, "some meeting", "2024-01-04 00:00:00", 80);
    private final EventRequestDto dto = TestData.createEventRequestDto("meeting", "2024-01-04 00:00:00", 1L);

    @Test
    public void testGetEventsByFilterIfFilterNullSuccess() {
        Event event2 = TestData.createEvent(2L, "party", "2024-01-07 10:00:00", 60);
        List<Event> events = List.of(event, event2);
        Mockito.when(eventRepository.findAll()).thenReturn(events);
        EventFilterDto filter = null;

        List<EventDto> filteredEvents = eventService.getEventsByFilter(filter);

        assertEquals(events.size(), filteredEvents.size());
    }

    @Test
    public void testGetEventsByFilterIfAllFiltersSuccess() {
        Event event2 = TestData.createEvent(2L, "party", "2024-01-07 10:00:00", 60);
        Event event3 = TestData.createEvent(3L, "workout", "2024-01-11 10:00:00", 40);
        List<Event> events = List.of(event, event2, event3);
        Mockito.when(eventRepository.findAll()).thenReturn(events);

        EventFilterDto filter = TestData.createEventFilterDto(
                event.getTitle(),
                event2.getStartDate().minusDays(1).toString(),
                event3.getMaxAttendees() + 1);

        List<EventDto> filteredEvents = eventService.getEventsByFilter(filter);

        assertEquals(events.size(), filteredEvents.size());
    }

    @Test
    public void testGetEventSuccess() {
        long id = event.getId();
        Mockito.when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        EventDto dto = eventService.getEvent(id);

        Mockito.verify(eventRepository, Mockito.times(1)).findById(id);
        assertEquals(event.getTitle(), dto.title());
    }

    @Test
    public void testGetEventIfNoEventExistsFailed() {
        long id = event.getId();
        Mockito.when(eventRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.getEvent(id));
    }

    @Test
    public void testCreateSuccess() {
        Mockito.when(userRepository.findById(dto.ownerId())).thenReturn(Optional.of(new User()));

        eventService.create(dto);

        Mockito.verify(eventRepository, Mockito.times(1)).save(captor.capture());

        Event capturedEvent = captor.getValue();
        assertEquals(dto.title(), capturedEvent.getTitle());
        assertEquals(dto.maxAttendees(), capturedEvent.getMaxAttendees());
    }

    @Test
    public void testCreateIfOwnerNotExistsFailed() {
        Mockito.when(userRepository.findById(dto.ownerId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.create(dto));
    }

    @Test
    public void testCreateIfInvalidSkillsFailed() {
        EventRequestDto dto = TestData.createEventRequestDto("meeting", 1L, List.of(1L));
        User owner = TestData.createUser(1L, List.of());
        event.setOwner(owner);
        Mockito.when(userRepository.findById(dto.ownerId())).thenReturn(Optional.of(event.getOwner()));
        Mockito.when(skillRepository.findAllById(dto.relatedSkillsIds())).thenReturn(List.of(new Skill()));

        assertThrows(DataValidationException.class, () -> eventService.create(dto));
    }

    @Test
    public void testUpdateSuccess() {
        User owner = TestData.createUser(1L, List.of());
        event.setOwner(owner);
        Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        eventService.update(dto, event.getId());

        Mockito.verify(eventRepository, Mockito.times(1)).save(captor.capture());

        Event capturedEvent = captor.getValue();
        assertEquals(dto.title(), capturedEvent.getTitle());
    }

    @Test
    public void testUpdateIfOwnerNotSameFailed() {
        long differentOwnerId = dto.ownerId() + 1;
        User owner = TestData.createUser(differentOwnerId, List.of());
        event.setOwner(owner);
        Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        assertThrows(DataValidationException.class, () -> eventService.update(dto, event.getId()));
    }

    @Test
    public void testUpdateIfInvalidSkillsFailed() {
        EventRequestDto dto = TestData.createEventRequestDto("meeting", 1L, List.of(1L));
        User owner = TestData.createUser(1L, List.of());
        event.setOwner(owner);

        Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        Mockito.when(skillRepository.findAllById(dto.relatedSkillsIds())).thenReturn(List.of(new Skill()));

        assertThrows(DataValidationException.class, () -> eventService.update(dto, event.getId()));
    }

    @Test
    public void testDeleteEventSuccess() {
        long id = event.getId();
        eventService.deleteEvent(id);

        Mockito.verify(eventRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    public void testGetOwnedEventsSuccess() {
        long userId = dto.ownerId();
        eventService.getOwnedEvents(userId);

        Mockito.verify(eventRepository, Mockito.times(1)).findAllByUserId(userId);
    }

    @Test
    public void testGetParticipatedEventsSuccess() {
        long userId = dto.ownerId();
        eventService.getParticipatedEvents(userId);

        Mockito.verify(eventRepository, Mockito.times(1)).findParticipatedEventsByUserId(userId);
    }
}

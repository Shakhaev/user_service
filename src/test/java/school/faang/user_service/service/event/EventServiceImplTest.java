package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.TestData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {
    @Mock
    private EventRepository eventRepository;
    @Spy
    private EventMapperImpl eventMapper;
    @Spy
    private SkillMapper skillMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Captor
    private ArgumentCaptor<Event> captor;
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        List<EventFilter> filters = TestData.createFilters();

        eventService = new EventServiceImpl(
                eventRepository,
                eventMapper,
                userRepository,
                skillRepository,
                filters);
    }

    @Test
    public void testGetEventsByFilterIfAllFiltersSuccess() {
        Event event1 = TestData.createEvent(1L, "test meeting", "2024-01-04T00:00:00", 40);
        Event event2 = TestData.createEvent(2L, "some meeting", "2024-01-07T00:00:00", 40);
        Event event3 = TestData.createEvent(3L, "best meeting", "2024-01-07T00:00:00", 60);
        Event event4 = TestData.createEvent(4L, "some meeting", "2024-01-04T00:00:00", 60);
        Mockito.when(eventRepository.findAll()).thenReturn(List.of(event1, event2, event3, event4));

        EventFilterDto filter = TestData.createEventFilterDto("some", "2024-01-06T00:00:00", 50);

        List<EventDto> filteredEvents = eventService.getEventsByFilter(filter);

        assertEquals(1, filteredEvents.size());
        assertTrue(filteredEvents.get(0).title().contains(filter.titleContains()));
        assertTrue(filteredEvents.get(0).startDate().isAfter(filter.startDateLaterThan()));
        assertTrue(filteredEvents.get(0).maxAttendees() < filter.maxAttendeesLessThan());
    }

    @Test
    public void testGetEventSuccess() {
        Event event = TestData.createEvent(1L, "some meeting", "2024-01-04T00:00:00", 80);
        long id = event.getId();
        Mockito.when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        EventDto dto = eventService.getEvent(id);

        Mockito.verify(eventRepository, Mockito.times(1)).findById(id);
        assertEquals(event.getTitle(), dto.title());
    }

    @Test
    public void testGetEventIfNoEventExistsFailed() {
        long id = 1L;
        Mockito.when(eventRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.getEvent(id));
    }

    @Test
    public void testCreateSuccess() {
        eventMapper.setSkillMapper(skillMapper);
        EventRequestDto dto = TestData.createEventRequestDto("meeting", "2024-01-04T00:00:00", 1L);
        Mockito.when(userRepository.findById(dto.ownerId())).thenReturn(Optional.of(new User()));

        eventService.createEvent(dto);

        Mockito.verify(eventRepository, Mockito.times(1)).save(captor.capture());
        Event capturedEvent = captor.getValue();
        assertEquals(dto.title(), capturedEvent.getTitle());
        assertEquals(dto.maxAttendees(), capturedEvent.getMaxAttendees());
    }

    @Test
    public void testCreateIfOwnerNotExistsFailed() {
        EventRequestDto dto = TestData.createEventRequestDto("meeting", "2024-01-04T00:00:00", 1L);
        Mockito.when(userRepository.findById(dto.ownerId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.createEvent(dto));
    }

    @Test
    public void testCreateIfInvalidSkillsFailed() {
        EventRequestDto dto = TestData.createEventRequestDto("meeting", 1L, List.of(1L));
        Mockito.when(userRepository
                .findById(dto.ownerId()))
                .thenReturn(Optional.of(TestData.createUser(1L, List.of())));
        Mockito.when(skillRepository.findAllById(dto.relatedSkillsIds())).thenReturn(List.of(new Skill()));

        assertThrows(DataValidationException.class, () -> eventService.createEvent(dto));
    }

    @Test
    public void testUpdateSuccess() {
        eventMapper.setSkillMapper(skillMapper);
        Event event = TestData.createEvent(1L, "some meeting", "2024-01-04T00:00:00", 80);
        event.setRelatedSkills(new ArrayList<>());
        User owner = TestData.createUser(1L, List.of());
        event.setOwner(owner);
        EventRequestDto dto = TestData.createEventRequestDto("meeting", "2024-01-04T00:00:00", 1L);
        Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        eventService.updateEvent(dto, event.getId());

        Mockito.verify(eventRepository, Mockito.times(1)).save(captor.capture());
        Event capturedEvent = captor.getValue();
        assertEquals(dto.title(), capturedEvent.getTitle());
    }

    @Test
    public void testUpdateIfOwnerNotSameFailed() {
        Event event = TestData.createEvent(1L, "some meeting", "2024-01-04T00:00:00", 80);
        EventRequestDto dto = TestData.createEventRequestDto("meeting", "2024-01-04T00:00:00", 1L);
        long differentOwnerId = dto.ownerId() + 1;
        User owner = TestData.createUser(differentOwnerId, List.of());
        event.setOwner(owner);
        Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        assertThrows(DataValidationException.class, () -> eventService.updateEvent(dto, event.getId()));
    }

    @Test
    public void testUpdateIfInvalidSkillsFailed() {
        Event event = TestData.createEvent(1L, "some meeting", "2024-01-04T00:00:00", 80);
        EventRequestDto dto = TestData.createEventRequestDto("meeting", 1L, List.of(1L));
        User owner = TestData.createUser(1L, List.of());
        event.setOwner(owner);
        Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        Mockito.when(skillRepository.findAllById(dto.relatedSkillsIds())).thenReturn(List.of(new Skill()));

        assertThrows(DataValidationException.class, () -> eventService.updateEvent(dto, event.getId()));
    }

    @Test
    public void testDeleteEventSuccess() {
        long id = 1L;
        eventService.deleteEvent(id);

        Mockito.verify(eventRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    public void testGetOwnedEventsSuccess() {
        long userId = 1L;
        eventService.getOwnedEvents(userId);

        Mockito.verify(eventRepository, Mockito.times(1)).findAllByUserId(userId);
    }

    @Test
    public void testGetParticipatedEventsSuccess() {
        long userId = 1L;
        eventService.getParticipatedEvents(userId);

        Mockito.verify(eventRepository, Mockito.times(1)).findParticipatedEventsByUserId(userId);
    }
}

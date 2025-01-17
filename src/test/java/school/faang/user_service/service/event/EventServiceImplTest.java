package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private Event event;
    private EventRequestDto dto;

    @BeforeEach
    public void setUp() {
        event = Event.builder()
                .id(1L)
                .title("meeting")
                .startDate(LocalDateTime.parse("2024-01-04T10:00:00"))
                .owner(User.builder().id(1L).build())
                .relatedSkills(new ArrayList<>())
                .description("opening meeting")
                .maxAttendees(70)
                .type(EventType.valueOf("PRESENTATION"))
                .status(EventStatus.valueOf("PLANNED"))
                .build();

        dto = EventRequestDto.builder()
                .title("new meeting")
                .startDate("2024-01-04 10:00:00")
                .ownerId(1L)
                .description("opening meeting")
                .maxAttendees(50)
                .eventType(EventType.valueOf("PRESENTATION"))
                .eventStatus(EventStatus.valueOf("PLANNED"))
                .build();
    }

    @Test
    public void testGetEventsByFilter() {
        EventFilterDto filter = EventFilterDto.builder()
                .titleContains("some")
                .startDateLaterThan("2024-01-05 10:00:00")
                .maxAttendeesLessThan(40)
                .build();

        eventService.getEventsByFilter(filter);

        Mockito.verify(eventRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void testGetEventThrowResourceNotFoundException() {
        long id = event.getId();
        Mockito.when(eventRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.getEvent(id));
    }

    @Test
    public void testGetEvent() {
        long id = event.getId();
        Mockito.when(eventRepository.findById(id)).thenReturn(Optional.of(event));

        eventService.getEvent(id);

        Mockito.verify(eventRepository, Mockito.times(1)).findById(id);
    }

    @Test
    public void testCreate() {
        Mockito.when(userRepository.findById(dto.ownerId())).thenReturn(Optional.of(new User()));

        eventService.create(dto);

        Mockito.verify(eventRepository, Mockito.times(1)).save(captor.capture());

        Event capturedEvent = captor.getValue();
        assertEquals(dto.title(), capturedEvent.getTitle());
        assertEquals(dto.maxAttendees(), capturedEvent.getMaxAttendees());
    }

    @Test
    public void testUpdate() {
        Mockito.when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        eventService.update(dto, event.getId());

        Mockito.verify(eventRepository, Mockito.times(1)).save(captor.capture());

        Event capturedEvent = captor.getValue();
        assertEquals(dto.title(), capturedEvent.getTitle());
        assertEquals(dto.maxAttendees(), capturedEvent.getMaxAttendees());
    }

    @Test
    public void testDeleteEvent() {
        long id = event.getId();
        eventService.deleteEvent(id);

        Mockito.verify(eventRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    public void testGetOwnedEvents() {
        long userId = event.getOwner().getId();
        eventService.getOwnedEvents(userId);

        Mockito.verify(eventRepository, Mockito.times(1)).findAllByUserId(userId);
    }

    @Test
    public void testGetParticipatedEvents() {
        long userId = event.getOwner().getId();
        eventService.getParticipatedEvents(userId);

        Mockito.verify(eventRepository, Mockito.times(1)).findParticipatedEventsByUserId(userId);
    }
}

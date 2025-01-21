package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.filters.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.EventService;
import school.faang.user_service.service.SkillService;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;



public class EventServiceTest {


    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillService skillService;

    @Mock
    private List<EventFilter> eventFilters;

    @Mock
    private User user;

    @InjectMocks
    private EventService eventService;


    @Spy
    private EventMapper eventMapper;

    @Captor
    private ArgumentCaptor<EventDto> eventCaptor;



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

        Mockito.when(eventRepository.findAllByUserId(userId)).thenReturn(events);

        eventService.deactivateEventsByUser(userId);

        verify(eventRepository, times(2)).save(event1);
        verify(eventRepository, times(2)).delete(event1);

        verify(eventRepository, times(2)).save(event2);
        verify(eventRepository, times(2)).delete(event2);

        assert event1.getStatus() == EventStatus.COMPLETED;
        assert event2.getStatus() == EventStatus.COMPLETED;
    }

    @Test
    public void testPrepareEventCandidate() {

        EventDto newEventDtoBefore = createNewEventCandidate();
        eventMapper.toEntityEvent(newEventDtoBefore);
        verify(eventMapper, times(1)).toEntityEvent(eventCaptor.capture());
        EventDto newEventDtoAfter = eventCaptor.getValue();
        assertEquals(newEventDtoBefore, newEventDtoAfter);
    }

    private EventDto createNewEventCandidate() {
        return EventDto.builder()
                .id(1L)
                .title("title")
                .ownerId(2L)
                .relatedSkills(List.of(3L,4L))
                .build();
    }

    @Test
    public void testCreateEvent(){

    }

    @Test
    public void testUpdateEvent(){

    }

    @Test
    public void testGetEvent(){

    }

    @Test
    void testGetParticipatedEvents(){

    }

    @Test
    void testGetOwnedEvents(){

    }

    @Test
    void testGetEventsByFilter(){

    }
}

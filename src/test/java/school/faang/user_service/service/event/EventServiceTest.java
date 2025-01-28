package school.faang.user_service.service.event;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.filters.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.EventMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.EventService;
import school.faang.user_service.service.SkillService;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@PrepareForTest(EventService.class)
public class EventServiceTest {

    User user1 = User.builder()
            .id(1L)
            .username("Mary")
            .email("user@gmail.com")
            .skills(getRelatedSkills())
            .build();
    User user2 = User.builder().id(2L).username("John").email("admin@gmail.com").build();


    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillService skillService;

    @Mock
    private List<EventFilter> eventFilters;

    @InjectMocks
    private EventService eventService;

    @Spy
    private EventMapper eventMapper = new EventMapperImpl();

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
                .relatedSkills(List.of(1L,4L))
                .build();
    }

    @Test
    public void testCreateEvent() throws Exception {

        EventDto eventDto = createNewEventCandidate();
        eventDto.setRelatedSkills(Arrays.asList(1L, 2L));
        List<Long> ownerSkillsIds = Arrays.asList(1L, 3L);
        EventService spyService = PowerMockito.spy(eventService);
        PowerMockito.doNothing().when(spyService, "validateEventRelatedSkills", ownerSkillsIds,
                eventDto.getRelatedSkills());
        when(userRepository.getUser(eventDto.getOwnerId())).thenReturn(user1);
        eventService.create(eventDto);
        EventDto result = spyService.create(eventDto);
        assertNotNull(result);
        PowerMockito.verifyPrivate(spyService).invoke("validateEventRelatedSkills", ownerSkillsIds,
                eventDto.getRelatedSkills());
    }

    @Test
    public void testCreateEvent_ThrowsException() throws Exception {
        EventDto eventDto = createNewEventCandidate();
        eventDto.setRelatedSkills(Arrays.asList(1L, 2L));
        List<Long> ownerSkillsIds = Arrays.asList(3L, 4L); // Намеренно не совпадают

        when(skillService.getSkillsIds(any())).thenReturn(ownerSkillsIds);

        assertThrows(BusinessException.class, () -> eventService.create(eventDto));
    }


    private static @NotNull List<Skill> getRelatedSkills() {
        return List.of(Skill.builder().id(1L).build(), Skill.builder().id(2L).build());
    }

    @Test
    public void testUpdateEvent(){

    }

    @Test
    public void testGetEvent(){


    }

    @Test
    public void testGetParticipatedEvents(){

    }

    @Test
    public void testGetOwnedEvents(){

    }

    @Test
    public void testGetEventsByFilter(){

    }
}

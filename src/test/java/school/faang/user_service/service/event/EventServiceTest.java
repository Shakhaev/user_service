package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFiltersDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;

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
    private UserService userService;
    @Mock
    private SkillService skillService;
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
    private List<Skill> skills;
    private List<Long> skillIds;

    @BeforeEach
    void setUp() {
        Skill firstSkill = Skill.builder().title("Java").build();
        Skill secondSkill = Skill.builder().title("Spring").build();
        skills = List.of(firstSkill, secondSkill);
        skillIds = eventMapper.mapSkillsToSkillIds(skills);
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
        Long userId = owner.getId();
        when(eventRepository.save(any())).thenReturn(event);
        when(userService.getUser(any())).thenReturn(owner);
        when(skillService.getSkills(any())).thenReturn(skills);

        Event createdEvent = eventService.create(event, userId, skillIds);

        assertNotNull(createdEvent);
        assertEquals(createdEvent.getOwner().getId(), userId);
        assertEquals(createdEvent.getRelatedSkills().size(), owner.getSkills().size());
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void testCreateEventInvalidSkills() {
        Long userId = owner.getId();
        Skill testSkill = Skill.builder().title("Python").build();
        List<Skill> modifiedSkillListForEvent = new ArrayList<>(owner.getSkills());
        modifiedSkillListForEvent.add(testSkill);
        List<Long> modifiedSkillIdsForEvent = eventMapper.mapSkillsToSkillIds(modifiedSkillListForEvent);

        when(userService.getUser(any())).thenReturn(owner);
        when(skillService.getSkills(any())).thenReturn(modifiedSkillListForEvent);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventService.create(event, userId, modifiedSkillIdsForEvent));
        assertEquals(String.format(
                "User with id %d don't have all related skills to create event id %d",
                userId, event.getId()), exception.getMessage());
    }

    @Test
    void testGetEvent() {
        Long eventId = event.getId();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        Event foundEvent = eventService.getEvent(eventId);

        assertNotNull(foundEvent);
        assertEquals(owner.getId(), foundEvent.getId());
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
        EventFiltersDto filters = EventFiltersDto.builder().title("Test").build();
        when(eventRepository.findAll()).thenReturn(Collections.singletonList(event));
        when(eventFilters.stream()).thenReturn(Stream.of(mock(EventFilter.class)));

        List<Event> filteredEvents = eventService.getEventsByFilter(filters);

        assertNotNull(filteredEvents);
        assertEquals(1, filteredEvents.size());
    }

    @Test
    void testDeleteEvent() {
        Long eventId = event.getId();
        doNothing().when(eventRepository).deleteById(eventId);

        eventService.deleteEvent(eventId);

        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    void testUpdateEvent() {
        Long eventId = event.getId();
        Event eventToUpdate = Event.builder()
                .id(eventId)
                .owner(owner)
                .build();
        List<Skill> relatedSkills = Collections.singletonList(Skill.builder().title("Java").build());
        List<Long> eventToUpdateSkillIds = eventMapper.mapSkillsToSkillIds(relatedSkills);
        eventToUpdate.setRelatedSkills(relatedSkills);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userService.getUser(any())).thenReturn(owner);
        when(skillService.getSkills(any())).thenReturn(skills);

        eventService.updateEvent(eventToUpdate, eventToUpdate.getId(), eventToUpdateSkillIds);

        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testUpdateEventNotFound() {
        Long eventId = event.getId();
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());
        when(userService.getUser(any())).thenReturn(owner);
        when(skillService.getSkills(any())).thenReturn(skills);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> eventService.updateEvent(event, eventId, skillIds));

        assertEquals(String.format("Event id %d not found", eventId), exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void testGetOwnedEvents() {
        Long ownerId = owner.getId();
        when(eventRepository.findAllByUserId(ownerId)).thenReturn(Collections.emptyList());

        eventService.getOwnedEvents(ownerId);

        verify(eventRepository, times(1)).findAllByUserId(ownerId);
    }

    @Test
    void testGetParticipatedEvents() {
        Long ownerId = owner.getId();
        when(eventRepository.findParticipatedEventsByUserId(ownerId)).thenReturn(Collections.emptyList());

        eventService.getParticipatedEvents(ownerId);

        verify(eventRepository, times(1)).findParticipatedEventsByUserId(ownerId);
    }
}

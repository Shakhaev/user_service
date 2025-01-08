package school.faang.user_service.controller.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.utility.validator.DataValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @Mock
    private EventService eventService;
    @Mock
    private UserService userService;
    @Mock
    private SkillService skillService;
    @Mock
    private DataValidator<EventDto> eventDtoValidator;
    @Spy
    private EventMapperImpl eventMapper;
    @InjectMocks
    private EventController controller;

    private Event event;
    private EventDto eventInputDto;
    private User testUser;
    private List<Skill> skills;
    private List<Long> skillIds;

    @BeforeEach
    void init() {
        Skill skill1 = Skill.builder().id(1L).title("Skill 1").build();
        Skill skill2 = Skill.builder().id(2L).title("Skill 2").build();
        skills = List.of(skill1, skill2);
        skillIds = List.of(skill1.getId(), skill2.getId());
        testUser = User.builder()
                .id(1L)
                .username("Username")
                .skills(skills)
                .build();
        eventInputDto = EventDto.builder()
                .id(null)
                .title("Event Title")
                .description("Event description")
                .maxAttendees(10)
                .ownerId(testUser.getId())
                .relatedSkillIds(skillIds)
                .build();
        event = eventMapper.toEntity(eventInputDto);
        event.setId(10L);
        event.setOwner(testUser);
        event.setRelatedSkills(skills);
    }

    @Test
    void testCreate() {
        when(userService.getUser(testUser.getId())).thenReturn(testUser);
        when(skillService.getSkills(skillIds)).thenReturn(skills);
        when(eventService.create(any(Event.class))).thenReturn(event);

        EventDto result = controller.create(eventInputDto);

        assertNotNull(result);
        checkEventDtoEqualsEvent(result, event);

        verify(eventService, times(1)).create(any(Event.class));
        verify(skillService, times(1)).getSkills(skillIds);
        verify(userService, times(1)).getUser(testUser.getId());
        verify(eventMapper, times(1)).toDto(event);
    }

    @Test
    void testGetEvent() {
        when(eventService.getEvent(event.getId())).thenReturn(event);
        EventDto result = controller.getEvent(event.getId());

        assertNotNull(result);
        checkEventDtoEqualsEvent(result, event);

        verify(eventService, times(1)).getEvent(any(Long.class));
    }

    @Test
    void testGetEventsByFilter() {
        EventFilters testFilter = EventFilters.builder()
                .title("title")
                .build();
        when(eventService.getEventsByFilter(any(EventFilters.class))).thenReturn(List.of(event));

        List<EventDto> result = controller.getEventsByFilter(testFilter);
        assertNotNull(result);

        EventDto firstResult = result.get(0);
        checkEventDtoEqualsEvent(firstResult, event);
        verify(eventService, times(1)).getEventsByFilter(testFilter);
    }

    @Test
    void deleteEvent() {
        controller.deleteEvent(event.getId());

        verify(eventService, times(1)).deleteEvent(event.getId());
    }

    @Test
    void updateEvent() {
        EventDto eventOutputDto = controller.updateEvent(eventInputDto);

        assertNotNull(eventOutputDto);
        verify(eventService, times(1)).updateEvent(any(Event.class));
    }

    @Test
    void getOwnedEvents() {
        List<EventDto> ownedEvents = controller.getOwnedEvents(testUser.getId());

        assertNotNull(ownedEvents);
        verify(eventService, times(1)).getOwnedEvents(testUser.getId());
    }

    @Test
    void getParticipatedEvents() {
        List<EventDto> participatedEvents = controller.getParticipatedEvents(testUser.getId());

        assertNotNull(participatedEvents);
        verify(eventService, times(1)).getParticipatedEvents(testUser.getId());
    }

    private void checkEventDtoEqualsEvent(EventDto firstResult, Event entity) {
        assertEquals(entity.getId(), firstResult.id());
        assertEquals(entity.getTitle(), firstResult.title());
        assertEquals(entity.getDescription(), firstResult.description());
        assertEquals(entity.getMaxAttendees(), firstResult.maxAttendees());
        assertEquals(entity.getOwner().getId(), firstResult.ownerId());
    }
}
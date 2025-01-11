package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilter;
import school.faang.user_service.dto.event.EventLocationFilter;
import school.faang.user_service.dto.event.EventOwnerIdFilter;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidateException;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.utils.event.EventPrepareData.getEvent;
import static school.faang.user_service.utils.event.EventPrepareData.getEventDto;
import static school.faang.user_service.utils.event.EventPrepareData.getFilterLocationDto;
import static school.faang.user_service.utils.event.EventPrepareData.getFilterOwnerDto;
import static school.faang.user_service.utils.event.EventPrepareData.getNewSkill;
import static school.faang.user_service.utils.event.EventPrepareData.getSkill;
import static school.faang.user_service.utils.event.EventPrepareData.getUser;
import static school.faang.user_service.utils.event.EventPrepareData.getUserWithNoSkills;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserService userService;

    @Mock
    private SkillService skillService;

    @Mock
    private EventMapperImpl eventMapper;

    private final List<EventFilter> eventFilters = new ArrayList<>();

    private EventService eventService;

    @BeforeEach
    public void init() {
        eventFilters.add(new EventLocationFilter());
        eventFilters.add(new EventOwnerIdFilter());
        eventService = new EventService(eventRepository, userService, skillService, eventMapper, eventFilters);
    }

    @Test
    public void testCreateEvent() {
        when(userService.findById(eq(1L))).thenReturn(getUser());
        when(skillService.findSkillById(eq(1L))).thenReturn(getSkill());
        when(eventMapper.toEntity(any())).thenReturn(getEvent());
        when(eventRepository.save(eq(getEvent()))).thenReturn(getEvent());
        when(eventMapper.toDto(eq(getEvent()))).thenReturn(getEventDto());

        EventDto resultEventDto = eventService.create(getEventDto());

        verify(eventRepository).save(eq(getEvent()));
        assertEquals(getEventDto(), resultEventDto);
    }

    @Test
    public void testCreateEventWhenUserHaveNotNeedSkills() {
        when(userService.findById(eq(1L))).thenReturn(getUser());
        when(skillService.findSkillById(eq(1L))).thenReturn(getNewSkill());

        assertThrows(DataValidateException.class, () -> eventService.create(getEventDto()));
    }

    @Test
    public void testGetEvent() {
        when(eventRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getEvent()));

        EventDto actualEvent = eventService.getEvent(1L);

        EventDto expectedEvent = eventMapper.toDto(getEvent());
        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    public void testGetEventWhenNotExist() {
        when(eventRepository.findById(anyLong())).thenThrow(DataValidateException.class);

        assertThrows(DataValidateException.class, () -> eventService.getEvent(anyLong()));
    }

    @Test
    public void testGetEventsByFilters() {
        when(eventRepository.findAll()).thenReturn(List.of(getEvent()));

        List<EventDto> resultEventsDto = eventService.getEventByFilters(getFilterLocationDto());

        verify(eventRepository).findAll();
        assertEquals(1, resultEventsDto.size());
    }

    @Test
    public void testGetEventsByFiltersWhenNotExist() {
        when(eventRepository.findAll()).thenReturn(List.of(getEvent()));

        List<EventDto> resultEventsDto = eventService.getEventByFilters(getFilterOwnerDto());

        verify(eventRepository).findAll();
        assertEquals(0, resultEventsDto.size());
    }

    @Test
    public void testDeleteEvent() {
        when(eventRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getEvent()));
        doNothing().when(eventRepository).deleteById(eq(1L));

        eventService.deleteEvent(1L);

        verify(eventRepository).deleteById(eq(1L));
    }

    @Test
    public void testDeleteEventWhenNotExist() {
        when(eventRepository.findById(eq(1L))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> eventService.deleteEvent(1L));
    }

    @Test
    public void testUpdateEvent() {
        when(eventRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getEvent()));
        when(userService.findById(eq(1L))).thenReturn(getUser());
        when(eventRepository.findAllByUserId(eq(1L))).thenReturn(List.of(getEvent()));
        when(skillService.findSkillById(1L)).thenReturn(Skill.builder().id(1L).build());
        when(eventMapper.toEntity(getEventDto())).thenReturn(getEvent());
        when(eventMapper.toDto(getEvent())).thenReturn(getEventDto());
        when(eventRepository.save(eq(getEvent()))).thenReturn(getEvent());

        EventDto eventDto = eventService.updateEvent(getEventDto());

        verify(eventRepository).save(eq(getEvent()));
        assertEquals(getEventDto(), eventDto);
    }

    @Test
    public void testUpdateWhenEventNotExist() {
        when(eventRepository.findById(eq(1L))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> eventService.updateEvent(getEventDto()));
    }

    @Test
    public void testUpdateWhenUserIsNotAuthorForEvent() {
        when(eventRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getEvent()));
        when(userService.findById(eq(1L))).thenReturn(getUserWithNoSkills());
        when(eventRepository.findAllByUserId(eq(1L))).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> eventService.updateEvent(getEventDto()));
    }

    @Test
    public void testGetOwnedEvent() {
        when(eventRepository.findAllByUserId(eq(1L))).thenReturn(List.of(getEvent()));
        when(userService.findById(eq(1L))).thenReturn(getUser());

        List<EventDto> ownedEvents = eventService.getOwnedEvents(1L);

        verify(eventRepository).findAllByUserId(anyLong());
        assertEquals(1, ownedEvents.size());
    }

    @Test
    public void testGetParticipatedEvents() {
        when(eventRepository.findParticipatedEventsByUserId(eq(1L))).thenReturn(List.of(getEvent()));
        when(userService.findById(eq(1L))).thenReturn(getUser());

        List<EventDto> ownedEvents = eventService.getParticipatedEvents(1L);

        verify(eventRepository).findParticipatedEventsByUserId(eq(1L));
        assertEquals(1, ownedEvents.size());
    }
}
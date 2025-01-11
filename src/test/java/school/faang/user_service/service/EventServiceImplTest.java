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
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
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
class EventServiceImplTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private EventMapperImpl eventMapper;

    private final List<EventFilter> eventFilters = new ArrayList<>();

    private EventServiceImpl eventServiceImpl;

    @BeforeEach
    public void init() {
        eventFilters.add(new EventLocationFilter());
        eventFilters.add(new EventOwnerIdFilter());
        eventServiceImpl = new EventServiceImpl(eventRepository, userRepository, skillRepository, eventMapper, eventFilters);
    }

    @Test
    public void testCreateEvent() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getUser()));
        when(skillRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getSkill()));
        when(eventMapper.toEntity(any())).thenReturn(getEvent());
        when(eventRepository.save(eq(getEvent()))).thenReturn(getEvent());
        when(eventMapper.toDto(eq(getEvent()))).thenReturn(getEventDto());

        EventDto resultEventDto = eventServiceImpl.create(getEventDto());

        verify(eventRepository).save(eq(getEvent()));
        assertEquals(getEventDto(), resultEventDto);
    }

    @Test
    public void testCreateEventWhenUserHaveNotNeedSkills() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getUser()));
        when(skillRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getNewSkill()));

        assertThrows(DataValidateException.class, () -> eventServiceImpl.create(getEventDto()));
    }

    @Test
    public void testGetEvent() {
        when(eventRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getEvent()));

        EventDto actualEvent = eventServiceImpl.getEvent(1L);

        EventDto expectedEvent = eventMapper.toDto(getEvent());
        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    public void testGetEventWhenNotExist() {
        when(eventRepository.findById(anyLong())).thenThrow(DataValidateException.class);

        assertThrows(DataValidateException.class, () -> eventServiceImpl.getEvent(anyLong()));
    }

    @Test
    public void testGetEventsByFilters() {
        when(eventRepository.findAll()).thenReturn(List.of(getEvent()));

        List<EventDto> resultEventsDto = eventServiceImpl.getEventByFilters(getFilterLocationDto());

        verify(eventRepository).findAll();
        assertEquals(1, resultEventsDto.size());
    }

    @Test
    public void testGetEventsByFiltersWhenNotExist() {
        when(eventRepository.findAll()).thenReturn(List.of(getEvent()));

        List<EventDto> resultEventsDto = eventServiceImpl.getEventByFilters(getFilterOwnerDto());

        verify(eventRepository).findAll();
        assertEquals(0, resultEventsDto.size());
    }

    @Test
    public void testDeleteEvent() {
        when(eventRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getEvent()));
        doNothing().when(eventRepository).deleteById(eq(1L));

        eventServiceImpl.deleteEvent(1L);

        verify(eventRepository).deleteById(eq(1L));
    }

    @Test
    public void testDeleteEventWhenNotExist() {
        when(eventRepository.findById(eq(1L))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> eventServiceImpl.deleteEvent(1L));
    }

    @Test
    public void testUpdateEvent() {
        when(eventRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getEvent()));
        when(userRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getUser()));
        when(eventRepository.findAllByUserId(eq(1L))).thenReturn(List.of(getEvent()));
        when(skillRepository.findById(1L)).thenReturn(Optional.ofNullable(Skill.builder().id(1L).build()));
        when(eventMapper.toEntity(getEventDto())).thenReturn(getEvent());
        when(eventMapper.toDto(getEvent())).thenReturn(getEventDto());
        when(eventRepository.save(eq(getEvent()))).thenReturn(getEvent());

        EventDto eventDto = eventServiceImpl.updateEvent(getEventDto());

        verify(eventRepository).save(eq(getEvent()));
        assertEquals(getEventDto(), eventDto);
    }

    @Test
    public void testUpdateWhenEventNotExist() {
        when(eventRepository.findById(eq(1L))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> eventServiceImpl.updateEvent(getEventDto()));
    }

    @Test
    public void testUpdateWhenUserIsNotAuthorForEvent() {
        when(eventRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getEvent()));
        when(userRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getUserWithNoSkills()));
        when(eventRepository.findAllByUserId(eq(1L))).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> eventServiceImpl.updateEvent(getEventDto()));
    }

    @Test
    public void testGetOwnedEvent() {
        when(eventRepository.findAllByUserId(eq(1L))).thenReturn(List.of(getEvent()));
        when(userRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getUser()));

        List<EventDto> ownedEvents = eventServiceImpl.getOwnedEvents(1L);

        verify(eventRepository).findAllByUserId(anyLong());
        assertEquals(1, ownedEvents.size());
    }

    @Test
    public void testGetParticipatedEvents() {
        when(eventRepository.findParticipatedEventsByUserId(eq(1L))).thenReturn(List.of(getEvent()));
        when(userRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(getUser()));

        List<EventDto> ownedEvents = eventServiceImpl.getParticipatedEvents(1L);

        verify(eventRepository).findParticipatedEventsByUserId(eq(1L));
        assertEquals(1, ownedEvents.size());
    }
}
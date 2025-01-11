package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.EventServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.utils.event.EventPrepareData.getEventDto;
import static school.faang.user_service.utils.event.EventPrepareData.getFilterLocationDto;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @Mock
    private EventServiceImpl eventServiceImpl;

    @InjectMocks
    private EventController eventController;

    @Test
    public void testCreateEvent() {
        EventDto eventDto = getEventDto();
        when(eventServiceImpl.create(eq(eventDto))).thenReturn(eventDto);

        EventDto resultEventDto = eventController.create(eventDto);

        verify(eventServiceImpl).create(eq(eventDto));
        assertEquals(eventDto, resultEventDto);
    }

    @Test
    public void testGetEvent() {
        EventDto eventDto = getEventDto();
        when(eventServiceImpl.getEvent(eq(1L))).thenReturn(eventDto);

        EventDto resultEventDto = eventController.getEvent(1L);

        verify(eventServiceImpl).getEvent(eq(1L));
        assertEquals(eventDto, resultEventDto);
    }

    @Test
    public void testGetEventsByFilter() {
        EventFilterDto filterDto = getFilterLocationDto();
        EventDto eventDto = getEventDto();
        when(eventServiceImpl.getEventByFilters(eq(filterDto))).thenReturn(List.of(eventDto));

        List<EventDto> resultEventsDto = eventController.getEventsByFilter(filterDto);

        verify(eventServiceImpl).getEventByFilters(eq(filterDto));
        assertEquals(List.of(eventDto), resultEventsDto);
    }

    @Test
    public void testDeleteEvent() {
        doNothing().when(eventServiceImpl).deleteEvent(anyLong());

        eventController.deleteEvent(1L);

        verify(eventServiceImpl).deleteEvent(anyLong());
    }

    @Test
    public void testUpdateEvent() {
        EventDto expectedEventDto = getEventDto();
        when(eventServiceImpl.updateEvent(eq(expectedEventDto))).thenReturn(expectedEventDto);

        EventDto actualEventDto = eventController.updateEvent(expectedEventDto);

        verify(eventServiceImpl).updateEvent(eq(expectedEventDto));
        assertEquals(expectedEventDto, actualEventDto);
    }

    @Test
    public void testGetOwnedEvent() {
        when(eventServiceImpl.getOwnedEvents(anyLong())).thenReturn(List.of(getEventDto()));

        List<EventDto> ownedEvents = eventController.getOwnedEvents(1L);

        verify(eventServiceImpl).getOwnedEvents(anyLong());
        assertEquals(1, ownedEvents.size());
    }

    @Test
    public void testGetParticipatedEvents() {
        when(eventServiceImpl.getParticipatedEvents(anyLong())).thenReturn(List.of(getEventDto()));

        List<EventDto> ownedEvents = eventController.getParticipatedEvents(1L);

        verify(eventServiceImpl).getParticipatedEvents(anyLong());
        assertEquals(1, ownedEvents.size());
    }
}
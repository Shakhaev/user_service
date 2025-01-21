package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.EventService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventController {


    private final EventService eventService;


    public EventDto create(EventDto event) {
        return eventService.create(event);
    }

    public EventDto update(EventUpdateDto eventDto, long eventId, long userId) {
        return eventService.updateEvent(eventDto, eventId, userId);
    }

    public EventDto getEvent(EventDto event) {
        return eventService.getEvent(event.getId());
    }

    public String deleteEvent(EventDto event) {
        return eventService.deleteEvent(event.getId());
    }

    public List<Event> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    public List<Event> getOwnedEvents(EventDto event){
        return eventService.getOwnedEvents(event.getOwnerId());
    }

    public List<Event> getEventsByFilter(EventDto eventDto,EventFilterDto filter,long userId){
        return eventService.getEventsByFilter(eventDto,filter,userId);
    }
}

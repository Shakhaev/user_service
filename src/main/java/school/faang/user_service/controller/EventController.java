package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.service.EventService;
import school.faang.user_service.validation.EventValidation;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventValidation validation;

    public EventDto create(EventDto event) {
        validation.validateEvent(event);
        return eventService.create(event);
    }

    public EventDto getEvent(Long eventId) {
        return eventService.getEvent(eventId);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    public void deleteEvent(long eventId) {
        eventService.deleteEvent(eventId);
    }

    public EventDto updateEvent(EventDto event) {
        validation.validateEvent(event);
        return eventService.updateEvent(event);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}

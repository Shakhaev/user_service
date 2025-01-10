package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.EventValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {
    private final EventService eventService;
    private final EventValidator eventValidator;

    @PostMapping("/create")
    public EventDto create(EventDto eventDto) {
        eventValidator.validateEventInfo(eventDto);
        return eventService.create(eventDto);
    }

    @GetMapping("/get-by-id")
    public EventDto getEvent(Long id) {
        return eventService.getEvent(id);
    }

    @GetMapping("/get-by-filter")
    public List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        return eventService.getEventsByFilter(eventFilterDto);
    }

    @GetMapping("/get-owned-events")
    public List<EventDto> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/get-participated-events")
    public List<EventDto> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    @DeleteMapping("/delete-event")
    public void deleteEvent(long id) {
        eventService.deleteEvent(id);
    }

    @PutMapping("/update-event")
    public EventDto updateEvent(EventDto event) {
        eventValidator.validateEventInfo(event);
        return eventService.updateEvent(event);
    }
}

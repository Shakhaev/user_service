package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

@RestController
@Validated
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/events")
    public EventDto create(@Valid @RequestBody EventDto event) {
        try {
            return eventService.create(event);
        } catch (Exception e) {
            throw new DataValidationException("Validation failed: " + e.getMessage(), e);
        }
    }

    @GetMapping("/events")
    public EventDto getEvent(@RequestParam("id") Long id) {
        return eventService.getEvent(id);
    }

    @PostMapping("/events/filter")
    public EventDto[] getEventsByFilter(@RequestBody EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @DeleteMapping("/events")
    public void deleteEvent(@RequestParam("id") Long id) {
        eventService.deleteEvent(id);
    }

    @PutMapping("/events")
    public EventDto updateEvent(@Valid @RequestBody EventDto event) {
        try {
            return eventService.updateEvent(event);
        } catch (Exception e) {
            throw new DataValidationException("Validation failed: " + e.getMessage(), e);
        }
    }

    @GetMapping("/events/owned")
    public EventDto[] getOwnedEvents(@RequestParam("id") Long id) {
        return eventService.getOwnedEvents(id);
    }

    @GetMapping("/events/participated")
    public EventDto[] getParticipatedEvents(@RequestParam("id") Long id) {
        return eventService.getParticipatedEvents(id);
    }
}

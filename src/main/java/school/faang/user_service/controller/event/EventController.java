package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<EventDto> create(@RequestBody EventDto event) {
        validateEvent(event);
        EventDto createdEvent = eventService.create(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEvent(@PathVariable long id) {
        EventDto event = eventService.getEvent(id);
        return ResponseEntity.ok(event);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<EventDto>> getEventsByFilter(@RequestBody EventFilterDto filter) {
        List<EventDto> events = eventService.getEventsByFilter(filter);
        return ResponseEntity.ok(events);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping
    public ResponseEntity<EventDto> updateEvent(@RequestBody EventDto eventDto) {
        validateEvent(eventDto);
        EventDto updatedEvent = eventService.updateEvent(eventDto);
        return ResponseEntity.ok(updatedEvent);
    }

    @GetMapping("/owned/{userId}")
    public ResponseEntity<List<EventDto>> getOwnedEvents(@PathVariable long userId) {
        List<EventDto> ownedEvents = eventService.getOwnedEvents(userId);
        return ResponseEntity.ok(ownedEvents);
    }

    @GetMapping("/participated/{userId}")
    public ResponseEntity<List<EventDto>> getParticipatedEvents(@PathVariable long userId) {
        List<EventDto> participatedEvents = eventService.getParticipatedEvents(userId);
        return ResponseEntity.ok(participatedEvents);
    }

    private void validateEvent(EventDto event) {
        if (event.title() == null || event.title().isEmpty()) {
            throw new DataValidationException("Event title must not be null or empty.");
        }
        if (event.startDate() == null) {
            throw new DataValidationException("Event start date must not be null.");
        }
        if (event.ownerId() == null) {
            throw new DataValidationException("Event must have an owner.");
        }
    }
}

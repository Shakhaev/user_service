package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import school.faang.user_service.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {
    private final EventService eventService;

    @PostMapping
    public EventDto create(@RequestBody @Valid EventDto event) {
        return eventService.create(event);
    }

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable("id") long id) {
        return eventService.getEvent(id);
    }

    @PostMapping("/filter")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filter) {
        return eventService.getEventByFilters(filter);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable long id) {
        eventService.deleteEvent(id);
    }

    @PutMapping
    public EventDto updateEvent(@RequestBody EventDto event) {
        return eventService.updateEvent(event);
    }

    @GetMapping("/by-owned/{userId}")
    public List<EventDto> getOwnedEvents(@PathVariable("userId") long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/by-owned-participated/{userId}")
    public List<EventDto> getParticipatedEvents(@PathVariable("userId") long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}

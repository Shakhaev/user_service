package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
@RestController
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping
    public EventDto getEvent(@RequestParam("id") Long id) {
        Event event = eventService.getEvent(id);
        return eventMapper.toDto(event);
    }

    @PostMapping
    public EventDto create(@Valid @RequestBody EventDto eventDto) {
        Event event = eventMapper.toEntity(eventDto);
        Event createdEvent = eventService.create(event);
        return eventMapper.toDto(createdEvent);
    }

    @PatchMapping
    public EventDto updateEvent(@Valid @RequestBody EventDto eventDto) {
        Event existingEvent = eventMapper.toEntity(eventDto);
        eventMapper.update(existingEvent, eventDto);
        Event updatedEvent = eventService.updateEvent(existingEvent);
        return eventMapper.toDto(updatedEvent);
    }

    @DeleteMapping
    public void deleteEvent(@RequestParam("id") Long id) {
        eventService.deleteEvent(id);
    }

    @PostMapping("/filter")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filter) {
        return eventMapper.toDto(eventService.getEventsByFilter(filter));
    }

    @GetMapping("/owned")
    public List<EventDto> getOwnedEvents(@RequestParam("id") Long id) {
        return eventMapper.toDto(eventService.getOwnedEvents(id));
    }

    @GetMapping("/participated")
    public List<EventDto> getParticipatedEvents(@RequestParam("id") Long id) {
        return eventMapper.toDto(eventService.getParticipatedEvents(id));
    }
}

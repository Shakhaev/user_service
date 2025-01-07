package school.faang.user_service.controller.event;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    @PostMapping("/create")
    public EventDto create(@NotNull @RequestBody EventDto event) {
        return eventService.create(event);
    }

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable long id) {
        return eventService.getEvent(id);
    }

    @PostMapping("/delete/{id}")
    public void deleteEvent(@PathVariable long id) {
        eventService.deleteEvent(id);
    }

    @GetMapping
    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @PostMapping("/update/{id}")
    public EventDto updateEvent(@PathVariable long id, @NotNull @RequestBody EventDto event) {
        return eventService.updateEvent(id, event);
    }
}

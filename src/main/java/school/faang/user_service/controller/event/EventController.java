package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;

    @PostMapping("/")
    public EventDto createEvent(@RequestBody CreateEventDto eventDto) {
        return eventService.createEvent(eventDto);
    }

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable long id) {
        return eventService.getEvent(id);
    }

    @GetMapping("/filter")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto eventFilterDto) {
        return eventService.getEventsByFilter(eventFilterDto);
    }

    @GetMapping("/owned")
    public List<EventDto> getOwnedEvents(@RequestParam long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/participated")
    public List<EventDto> getParticipatedEvents(@RequestParam long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    @DeleteMapping("/")
    public void deleteEvent(@RequestParam long id) {
        eventService.deleteEvent(id);
    }

    @PutMapping("/")
    public EventDto updateEvent(@RequestBody UpdateEventDto event) {
        return eventService.updateEvent(event);
    }
}

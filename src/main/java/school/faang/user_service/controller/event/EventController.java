package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventForClientDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;

    @PostMapping("/create-event")
    public EventForClientDto createEvent(@RequestBody EventCreateDto eventDto) {
        return eventService.createEvent(eventDto);
    }

    @GetMapping("/get-event-by-id")
    public EventForClientDto getEvent(@RequestParam long id) {
        return eventService.getEvent(id);
    }

    @GetMapping("/get-events-by-filter")
    public List<EventForClientDto> getEventsByFilter(@RequestBody EventFilterDto eventFilterDto) {
        return eventService.getEventsByFilter(eventFilterDto);
    }

    @GetMapping("/get-owned-events")
    public List<EventForClientDto> getOwnedEvents(@RequestParam long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/get-participated-events")
    public List<EventForClientDto> getParticipatedEvents(@RequestParam long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    @DeleteMapping("/delete-event")
    public void deleteEvent(@RequestParam long id) {
        eventService.deleteEvent(id);
    }

    @PutMapping("/update-event")
    public EventForClientDto updateEvent(@RequestBody EventUpdateDto event) {
        return eventService.updateEvent(event);
    }
}

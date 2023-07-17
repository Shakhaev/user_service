package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import school.faang.user_service.service.event.EventService;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/event")
    public EventDto createEvent(@RequestBody EventDto eventDto) {
        validateEvent(eventDto);
        return eventService.createEvent(eventDto);
    }

    @DeleteMapping("/event/{id}")
    public void deleteEvent(@PathVariable Long id) {
        validateId(id);
        eventService.deleteEvent(id);
    }
  
    @GetMapping("/event/{id}")
    public EventDto getEvent(@PathVariable Long id) {
        validateId(id);
        return eventService.getEvent(id);
    }

    public void validateEvent(EventDto eventDto) {
        if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()) {
            throw new DataValidationException("Event title cannot be empty");
        }

        if (eventDto.getStartDate() == null) {
            throw new DataValidationException("Event start date cannot be null");
        }

        if (eventDto.getOwnerId() == null || eventDto.getOwnerId() < 0) {
            throw new DataValidationException("Event owner ID cannot be null");
        }
    }
  
    private void validateId(long id) {
        if (id < 0) {
            throw new DataValidationException("Id cannot be negative");
        }
    }
}
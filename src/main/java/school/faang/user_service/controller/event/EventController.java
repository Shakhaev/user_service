package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.service.event.EventService;

@Component
@RequiredArgsConstructor
public class EventController {


    private final EventService eventService;


    public EventDto create(EventDto event) {
        validation(event);
        return eventService.create(event);
    }

    private void validation(EventDto event) throws DataIntegrityViolationException {
        if (event.getDescription().isBlank() || event.getOwnerId() == null || event.getStartDate() == null) {
            throw new DataIntegrityViolationException("Cannot create event without owner id, " +
                    "blank or empty description, start date and end date");
        }
    }
}

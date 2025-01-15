package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFiltersDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.utility.validator.impl.EventDtoValidator;
import school.faang.user_service.utility.validator.impl.EventFiltersDtoValidator;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class EventController {
    private final EventService eventService;
    private final EventDtoValidator eventDtoValidator;
    private final EventFiltersDtoValidator eventFilterValidator;
    private final EventMapper eventMapper;

    public EventDto create(EventDto eventDto) {
        eventDtoValidator.validate(eventDto);
        Event inputEvent = eventMapper.toEntity(eventDto);

        Event createdEvent = eventService.create(inputEvent, eventDto.ownerId(), eventDto.relatedSkillIds());
        return eventMapper.toDto(createdEvent);
    }

    public EventDto getEvent(long id) {
        Event event = eventService.getEvent(id);
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFiltersDto filters) {
        eventFilterValidator.validate(filters);
        List<Event> events = eventService.getEventsByFilter(filters);
        return eventMapper.toDtoList(events);
    }

    public void deleteEvent(long eventId) {
        eventService.deleteEvent(eventId);
    }

    public EventDto updateEvent(EventDto eventDto, Long ownerId, List<Long> relatedSkillIds) {
        eventDtoValidator.validate(eventDto);
        Event inputEvent = eventMapper.toEntity(eventDto);

        eventService.updateEvent(inputEvent, ownerId, relatedSkillIds);
        return eventMapper.toDto(inputEvent);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> ownedEvents = eventService.getOwnedEvents(userId);
        return eventMapper.toDtoList(ownedEvents);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> participatedEvents = eventService.getParticipatedEvents(userId);
        return eventMapper.toDtoList(participatedEvents);
    }
}

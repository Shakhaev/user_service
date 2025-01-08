package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.utility.validator.DataValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final UserService userService;
    private final SkillService skillService;
    private final DataValidator<EventDto> eventDtoValidator;
    private final DataValidator<EventFilters> eventFilterValidator;
    private final EventMapper eventMapper;

    public EventDto create(EventDto eventDto) {
        eventDtoValidator.validate(eventDto);
        Event inputEvent = createFromDtoAddUserAndSkills(eventDto);

        Event createdEvent = eventService.create(inputEvent);
        return eventMapper.toDto(createdEvent);
    }

    public EventDto getEvent(long id) {
        Event event = eventService.getEvent(id);
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilters filters) {
        eventFilterValidator.validate(filters);

        List<Event> events = eventService.getEventsByFilter(filters);
        return events.stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public void deleteEvent(long eventId) {
        eventService.deleteEvent(eventId);
    }

    public EventDto updateEvent(EventDto event) {
        eventDtoValidator.validate(event);
        Event inputEvent = createFromDtoAddUserAndSkills(event);
        eventService.updateEvent(inputEvent);
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


    private Event createFromDtoAddUserAndSkills(EventDto eventDto) {
        User owner = userService.getUser(eventDto.ownerId());
        List<Skill> skills = skillService.getSkills(eventDto.relatedSkillIds());

        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(owner);
        event.setRelatedSkills(skills);
        return event;
    }
}

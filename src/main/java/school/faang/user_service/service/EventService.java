package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validation.EventValidation;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventValidation validation;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;

    public EventDto create(EventDto eventDto) {
        validation.validateEvent(eventDto);
        validation.validateUserSkills(eventDto);
        Event event = eventMapper.toEntity(eventDto);
        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toDto(updatedEvent);
    }

    public EventDto getEvent(long eventId) {
        return eventRepository.findById(eventId)
                .map(eventMapper::toDto)
                .orElseThrow(() ->
                        new NoSuchElementException(String.format("Не найден ивент с айди %d", eventId)));
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        Stream<Event> events = eventRepository.findAll().stream();
        eventFilters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .forEach(eventFilter -> eventFilter.apply(events, filter));
        return events.map(eventMapper::toDto).toList();
    }

    public void deleteEvent(long eventId) {
        validation.validateEventId(eventId);
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        validation.validateEventId(eventDto.getId());
        validation.validateEvent(eventDto);
        validation.validateEventOwner(eventDto);
        validation.validateUserSkills(eventDto);

        Event event = eventMapper.toEntity(eventDto);
        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toDto(updatedEvent);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }
}

package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventForClientDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final EventValidator eventValidator;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;

    public EventForClientDto create(EventCreateDto eventCreateDto) {
        Event event = eventMapper.fromCreateDtoToEntity(eventCreateDto);
        eventValidator.validateEventInfo(event);
        eventValidator.validateEventCreatorSkills(eventCreateDto);

        Event savedEvent = eventRepository.save(event);
        return eventMapper.toForClientDto(savedEvent);
    }

    public EventForClientDto getEvent(long id) {
        return eventMapper.toForClientDto(eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with such id not found!" + id)));
    }

    public List<EventForClientDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        Stream<Event> eventStream = eventRepository.findAll().stream();
        for (EventFilter filter : eventFilters) {
            if (filter.isApplicable(eventFilterDto)) {
                eventStream = filter.apply(eventStream, eventFilterDto);
            }
        }
        return eventStream
                .map(eventMapper::toForClientDto)
                .toList();
    }

    public void deleteEvent(long id) {
        eventRepository.deleteById(id);
    }

    public EventForClientDto updateEvent(EventUpdateDto eventUpdateDto) {
        eventValidator.validateEventInfo(eventMapper.fromUpdateDtoToEntity(eventUpdateDto));
        Event event = eventRepository.findById(eventUpdateDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found by id: " + eventUpdateDto.getId()));
        eventMapper.update(event, eventUpdateDto);
        event = eventRepository.save(event);
        return eventMapper.toForClientDto(event);
    }

    public List<EventForClientDto> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId)
                .stream()
                .map(eventMapper::toForClientDto)
                .toList();
    }

    public List<EventForClientDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId)
                .stream()
                .map(eventMapper::toForClientDto)
                .toList();
    }
}


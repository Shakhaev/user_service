package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final EventMapper eventMapper;
    private final EventDtoValidator eventValidator;
    private final List<EventFilter> eventFilters;

    public EventDto create(EventDto eventDto) {
        eventValidator.validate(eventDto);

        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(userService.findById(eventDto.getOwnerId()));
        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    public EventDto getEvent(long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("There is no event with this number"));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilters(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(events, filters));

        return eventMapper.toListDto(events.toList());
    }
}

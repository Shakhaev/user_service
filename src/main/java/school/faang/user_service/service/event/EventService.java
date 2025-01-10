package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import jakarta.persistence.EntityNotFoundException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
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
    private final UserMapper userMapper;
    private final UserService userService;
    private final SkillMapper skillMapper;
    private final SkillService skillService;
    private final List<EventFilter> eventFilters;


    public EventDto create(EventDto eventDto) {
        eventValidator.validateEventCreatorSkills(eventDto);
        Event event = eventMapper.toEntity(eventDto);

        event.setRelatedSkills(eventDto.getRelatedSkillsIds()
                .stream()
                .map(skillService::findSkillById)
                .map(skillMapper::toEntity)
                .toList());
        event.setOwner(userMapper.toEntity(userService.findUserById(eventDto.getOwnerId())));

        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDto(savedEvent);
    }

    public EventDto getEvent(long id) {
        return eventMapper.toDto(eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with such id not found!")));
    }

    //test
    public List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        Stream<Event> eventStream = eventRepository.findAll().stream();
        eventFilters.stream()
                .filter(filter -> filter.isApplicable(eventFilterDto))
                .forEach(filter -> filter.apply(eventStream, eventFilterDto));
        return eventStream.map(eventMapper::toDto).toList();
    }

    //test
    public void deleteEvent(long id) {
        eventRepository.deleteById(id);
    }

    //test
    public EventDto updateEvent(EventDto eventDto) {
        Event event = eventRepository.findById(eventDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found by id: " + eventDto.getId()));
        eventMapper.update(event, eventDto);
        event = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(event);
    }

    //test
    public List<EventDto> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId)
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    //test
    public List<EventDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId)
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }
}


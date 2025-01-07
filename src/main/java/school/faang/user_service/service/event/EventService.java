package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.annotation.ReadTransactional;
import school.faang.user_service.annotation.WriteTransactional;
import school.faang.user_service.check.event.EventCheck;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filter.EventFilter;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventCheck eventCheck;
    private final EventRepository eventRepository;
    private final SkillService skillService;
    private final UserService userService;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;

    @WriteTransactional
    public EventDto create(EventDto eventDto) {
        eventCheck(eventDto);

        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(userService.getUserById(eventDto.getOwnerId()));
        event.setRelatedSkills(skillService.getSkillListBySkillIds(eventDto.getRelatedSkillIds()));
        return eventMapper.toDto(eventRepository.save(event));
    }

    @ReadTransactional
    public EventDto getEvent(long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Событие по id: %s не найдено!", id)));
        return eventMapper.toDto(event);
    }

    @ReadTransactional
    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(events, filters));
        return eventMapper.toDto(events.toList());
    }

    @WriteTransactional
    public void deleteEvent(long id) {
        eventRepository.deleteById(id);
    }

    @WriteTransactional
    public EventDto updateEvent(long id, EventDto eventDto) {
        eventCheck(eventDto);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Событие по id: %s не найдено!", id)));
        eventMapper.update(event, eventDto);
        event.setRelatedSkills(skillService.getSkillListBySkillIds(eventDto.getRelatedSkillIds()));
        return eventMapper.toDto(eventRepository.save(event));
    }

    private void eventCheck(EventDto eventDto) {
        eventCheck.eventCheck(eventDto);
        if (!eventCheck.userHasSkills(eventDto.getOwnerId(), eventDto.getRelatedSkillIds())) {
            throw new DataValidationException("Пользователь не может провести такое событие с такими навыками");
        }
    }
}

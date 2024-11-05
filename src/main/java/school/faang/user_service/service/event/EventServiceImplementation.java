package school.faang.user_service.service.event;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;

@Service
@Data
public class EventServiceImplementation implements EventService {

    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    @Autowired
    private List<EventValidator> eventValidators;
    @Autowired
    private List<EventFilter> eventFilters;

    @PostConstruct
    public void init() {
        System.out.println("Number of EventValidators injected: " + eventValidators.size());
        System.out.println("Number of EventFilters injected: " + eventFilters.size());
        eventValidators.forEach(validator -> System.out.println("Validator: " + validator.getClass().getSimpleName()));
        eventFilters.forEach(filter -> System.out.println("Filter: " + filter.getClass().getSimpleName()));
    }

    @Override
    public EventDto create(EventDto eventDto) {
        validateEvent(eventDto);

        Event event = eventMapper.toEntity(eventDto);
        List<Skill> skills = skillRepository.findSkillsByGoalId(eventDto.getId());
        User owner = userRepository.getReferenceById(eventDto.getOwnerId());
        event.setRelatedSkills(skills);
        event.setOwner(owner);

        return eventMapper.toDto(eventRepository.save(event));
    }

    @Override
    public EventDto getEvent(Long id) {
        Event event = eventRepository.findById(id).orElse(null);
        return eventMapper.toDto(event);
    }

    @Override
    public List<EventDto> getEventsByFilter(EventFilterDto filterDto) {
        List<Event> events = eventRepository.findAll();

        List<EventFilter> applicableEventFilters = eventFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto)).toList();
        List<Event> filteredEvents = events.stream()
                .filter(event -> applicableEventFilters.stream().allMatch(filter -> filter.apply(event, filterDto)))
                .toList();
        return filteredEvents.stream().map(eventMapper::toDto).toList();
    }

    @Override
    public List<EventDto> getOwnedEvents(Long userId) {
        if (userId == null) {
            return null;
        }
        List<Event> events = eventRepository.findAllByUserId(userId);
        return events.stream().map(eventMapper::toDto).toList();
    }

    private void validateEvent(EventDto eventDto) {
        for (EventValidator validator : eventValidators) {
            if (!validator.isValid(eventDto)) {
                throw new DataValidationException(validator.getMessage());
            }
        }
    }


}

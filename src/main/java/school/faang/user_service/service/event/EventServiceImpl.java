package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventRequestDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final List<EventFilter> filters;

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventRepository.findAll().stream()
                .filter(event -> isEventMatchingFilter(event, filter))
                .map(eventMapper::toDto)
                .toList();
    }

    public EventDto getEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> logAndThrowResourceNotFoundException("Event", id));

        return eventMapper.toDto(event);
    }

    public EventDto create(EventRequestDto eventData) {
        Event event = eventMapper.toEntity(eventData);

        long ownerId = eventData.ownerId();
        event.setOwner(userRepository.findById(ownerId)
                .orElseThrow(() -> logAndThrowResourceNotFoundException("User", ownerId)));

        List<Skill> relatedSkills = getRelatedSkills(eventData.relatedSkillsIds());
        validateSkills(relatedSkills, event.getOwner());
        event.setRelatedSkills(relatedSkills);

        eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    public EventDto update(EventRequestDto eventData, Long id) {
        long ownerId = eventData.ownerId();
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> logAndThrowResourceNotFoundException("Event", id));
        validateOwner(ownerId, event);

        eventMapper.update(eventData, event);

        List<Skill> relatedSkills = getRelatedSkills(eventData.relatedSkillsIds());
        validateSkills(relatedSkills, event.getOwner());
        event.getRelatedSkills().clear();
        event.getRelatedSkills().addAll(relatedSkills);

        eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public List<EventDto> getOwnedEvents(Long userId) {
        return eventRepository.findAllByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventDto> getParticipatedEvents(Long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }

    private boolean isEventMatchingFilter(Event event, EventFilterDto filter) {
        return filters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .allMatch(eventFilter -> eventFilter.test(filter, event));
    }

    private ResourceNotFoundException logAndThrowResourceNotFoundException(String entityName, Long id) {
        log.error("{} with id {} was not found", entityName, id);
        return new ResourceNotFoundException(entityName + " with id " + id + " was not found");
    }

    private List<Skill> getRelatedSkills(List<Long> relatedSkillsIds) {
        return relatedSkillsIds == null ? new ArrayList<>() : relatedSkillsIds.stream()
                .map(id -> skillRepository.findById(id)
                        .orElseThrow(() -> logAndThrowResourceNotFoundException("Skill", id)))
                .toList();
    }

    private void validateSkills(List<Skill> relatedSkills, User owner) {
        List<Skill> ownerSkills = owner.getSkills();
        for (var skill : relatedSkills) {
            if (!ownerSkills.contains(skill)) {
                log.error("User with id {} doesn't have enough skills to be the event owner", owner.getId());
                throw new DataValidationException("User with id " + owner.getId()
                        + " doesn't have enough skills to be the event owner");
            }
        }
    }

    private void validateOwner(long ownerId, Event event) {
        if (ownerId != event.getOwner().getId()) {
            log.error("User with id {} can't update event with id {}, because he is not this event owner",
                    ownerId, event.getId());
            throw new DataValidationException("User with id " + ownerId + " can't update event with id "
                    + event.getId() + ", because he is not this event owner");
        }
    }
}

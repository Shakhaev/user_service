package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilter;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidateException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final SkillService skillService;
    private final EventMapper eventMapper;
    private final List<EventFilter> filters;

    @Transactional
    public EventDto create(EventDto event) {
        User ownerUser = getUserById(event.getOwnerId());
        validateUserHaveCurrentSkills(event, ownerUser);
        Event entity = eventMapper.toEntity(event);
        Event savedEntity = eventRepository.save(entity);
        return eventMapper.toDto(savedEntity);

    }

    public EventDto getEvent(long id) {
        return eventMapper.toDto(getEventById(id));
    }

    public List<EventDto> getEventByFilters(EventFilterDto filter) {
        List<Event> events = eventRepository.findAll();
        events = getFilteredEvents(filter, events);
        return events.stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteEvent(long id) {
        validateEventExist(id);
        eventRepository.deleteById(id);
    }

    @Transactional
    public EventDto updateEvent(EventDto eventDto) {
        long id = eventDto.getId();
        validateEventExist(id);
        User user = getUserById(eventDto.getOwnerId());
        validateUserIsAuthorForEvent(eventDto, user);
        validateUserHaveCurrentSkills(eventDto, user);
        Event updatedEvent = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(updatedEvent);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        validateUser(userId);
        return eventRepository.findAllByUserId(userId)
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        validateUser(userId);
        return eventRepository.findParticipatedEventsByUserId(userId)
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    private boolean isHaveUserSkills(User user, List<Long> skillsIds) {
        List<Skill> skills = getSkillsByIds(skillsIds);
        return new HashSet<>(user.getSkills()).containsAll(skills);
    }

    private User getUserById(long userId) {
        return userService.findById(userId);
    }

    private Event getEventById(long id) {
        return eventRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Event with id = " + id + " doesn't exist"));
    }

    private List<Event> getFilteredEvents(EventFilterDto filter, List<Event> events) {
        for (EventFilter eventFilter : filters) {
            if (eventFilter.isApplicable(filter)) {
                events = eventFilter.apply(events, filter);
            }
        }
        return events;
    }

    private void validateEventExist(long id) {
        if (!isExistEvent(id)) {
            throw new IllegalArgumentException("Event with id = " + id + " doesn't exist");
        }
    }

    private void validateUserIsAuthorForEvent(EventDto eventDto, User user) {
        boolean isNotAuthorForEvent = getOwnedEvents(user.getId()).stream()
                .noneMatch(e -> e.getId().equals(eventDto.getId()));
        if (isNotAuthorForEvent) {
            throw new IllegalArgumentException("User is not author for event with id = " + eventDto.getId());
        }
    }

    private void validateUserHaveCurrentSkills(EventDto eventDto, User user) {
        if (!isHaveUserSkills(user, eventDto.getRelatedSkills())) {
            throw new DataValidateException(
                    "User can't conduct such an event skills with ids " + eventDto.getRelatedSkills());
        }
    }

    private void validateUser(long userId) {
        if (userService.findById(userId) == null) {
            throw new IllegalArgumentException("User with id = " + userId + " doesn't exist");
        }
    }

    private List<Skill> getSkillsByIds(List<Long> skillsIds) {
        return skillsIds.stream()
                .map(skillService::findSkillById)
                .toList();
    }

    private boolean isExistEvent(Long id) {
        return eventRepository.findById(id).isPresent();
    }
}
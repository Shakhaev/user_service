package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;


import java.util.List;
import java.util.stream.Stream;


@Component
@RequiredArgsConstructor
public class EventService {

    private final EventMapper eventMapper;
    private final SkillService skillService;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final List<EventFilter> eventFilters;

    @Transactional
    public EventDto create(EventDto eventDto) throws DataValidationException {
        Event event = eventMapper.toEntityEvent(eventDto);
        User owner = getUser(eventDto.getOwnerId());
        event.setOwner(owner);
        validateEventRelatedSkills(eventDto.getRelatedSkills(), getSkillsIds(owner.getSkills()));
        event.setRelatedSkills(skillService.getAllSkills(eventDto.getRelatedSkills()));
        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    @Transactional
    public EventDto updateEvent(EventUpdateDto eventDto, long eventId, long userId) {
        Event event = eventRepository.getById(eventId);
        User owner = event.getOwner();
        if (!eventDto.getRelatedSkillIds().isEmpty()) {
            validateEventRelatedSkills(
                    eventDto.getRelatedSkillIds(),
                    getSkillsIds(owner.getSkills())
            );
            event.setRelatedSkills(skillService.getAllSkills(eventDto.getRelatedSkillIds()));
        }
        if (eventDto.getOwnerId() != null) {
            if (event.getOwner().getId() != userId) {
                throw new BusinessException("Основателя может сменить только основатель");
            }
        }
        eventMapper.updateEntityFromDto(event,eventDto);
        eventRepository.save(event);
        return eventMapper.toDto(event);
    }


    public EventDto getEvent(long eventId) {
        return eventMapper.toDto(eventRepository.getById(eventId));
    }

    @Transactional
    public String deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new DataValidationException("Event with id " + eventId + " not found");
        }
        eventRepository.deleteById(eventId);
        return "Event with id " + eventId + " deleted";
    }

    public List<Event> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }

    public List<EventDto> getOwnedEvents(Long ownerId) {
        return eventRepository.findAllByUserId(ownerId).stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filterDto) {
        Stream<EventDto> events = eventRepository.findAll().stream()
                .map(eventMapper::toDto);
        for (EventFilter filter : eventFilters) {
            events = filter.apply(events, filterDto);
        }
        return events.toList();
    }

    @Transactional
    public void deactivateEventsByUser(Long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);

        events.forEach(event -> {
            event.setStatus(EventStatus.COMPLETED);
            eventRepository.save(event);
            eventRepository.delete(event);
        });
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("Пользователь не найден"));
    }

    private List<Long> getSkillsIds(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }

    private void validateEventRelatedSkills(List<Long> relatedSkills, List<Long> ownerSkillsIds) {
        for (Long skillId : relatedSkills) {
            if (!ownerSkillsIds.contains(skillId)) {
                throw new BusinessException("Пользователь не обладает скилами" + skillId + " для создания события");
            }
        }
    }

}

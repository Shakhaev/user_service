package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.Filter;
import school.faang.user_service.filters.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


@Component
@RequiredArgsConstructor
public class EventService {

    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final List<EventFilter> eventFilters;

    public EventDto create(EventDto event) throws DataValidationException {

        Event newSaveEvent = eventRepository.save(prepareEventCandidate(event));

        return eventMapper.toDto(newSaveEvent);

    }

    public EventDto updateEvent(EventDto event) throws DataValidationException {

        User user = getOwner(event);
        List<Long> ownerListIdSkills = getOwnerListIdSkills(event);
        if (ownerListIdSkills.isEmpty()) {
            throw new DataValidationException("Пользователь не имеет скилов");
        }
        Event newEventCandidate = eventMapper.toEntityEvent(event);

        return null;
    }

    public EventDto getEvent(long eventId) {
        return eventMapper.toDto(eventRepository.getReferenceById(eventId));
    }

    public String deleteEvent(Long eventId) {
        Long id = eventRepository.getReferenceById(eventId).getId();
        eventRepository.deleteById(id);
        return "Event with id " + id + " deleted";
    }

    public List<Event> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }

    public List<Event> getOwnedEvents(Long ownerId) {
        return eventRepository.findAllByUserId(ownerId);
    }

    public List<Event> getEventsByFilter(EventDto eventDto,EventFilterDto eventFilter, Long userId) {
        List<Event> newEventCandidate;
        Stream<Event> ownedEvents= getOwnedEvents(userId).stream();
        newEventCandidate = ownedEvents.filter(event -> {
            return eventFilters.stream()
                    .filter(filter ->filter.isApplicable(eventMapper.toDto(event)))
                    .filter(filter-> filter.apply(ownedEvents,eventDto).isParallel())
                    .isParallel();
        }
        ).toList();


        return newEventCandidate;
    }

    private User getOwner(EventDto event) throws DataValidationException {
        Long ownerId = event.getOwnerId();
        return userRepository.findById(ownerId)
                .orElseThrow(() -> new DataValidationException("User not found"));
    }

    private List<Long> getOwnerListIdSkills(EventDto event) {
        List<Long> skillsId = new ArrayList<>();
        User owner = getOwner(event);
        skillsId = owner.getSkills().stream()
                .map(Skill::getId)
                .toList();
        return skillsId;
    }

    private List<Skill> checkGetUserSkills(EventDto event) throws DataValidationException {
        Map<User, List<Skill>> userSkills = new HashMap<>();
        Long ownerId = event.getOwnerId();

        User skillOwner = userRepository.findById(ownerId)
                .orElseThrow(() -> new DataValidationException("User not found"));


        List<Long> ownerListIdSkills = getOwnerListIdSkills(event);
        if (ownerListIdSkills.isEmpty()) {
            throw new DataValidationException("User hasn't skills");
        }

        return skillOwner.getSkills();
    }

    private Event prepareEventCandidate(EventDto event) {

        Event newEventCandidate = eventMapper.toEntityEvent(event);
        newEventCandidate.setOwner(getOwner(event));
        newEventCandidate.setRelatedSkills(checkGetUserSkills(event));
        return eventMapper.toEntityEvent(event);
    }
}

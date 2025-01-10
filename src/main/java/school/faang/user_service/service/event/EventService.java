package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventFiltersDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final UserService userService;
    private final SkillService skillService;
    private final EventRepository eventRepository;
    private final List<EventFilter> eventFilters;
    private final EventMapper eventMapper;

    public Event create(Event inputEvent, Long ownerId, List<Long> relatedSkillIds) {
        log.info("Creating Event with owner id {}", ownerId);
        Event event = addUserAndSkillsForInputEvent(inputEvent, ownerId, relatedSkillIds);
        validateOwnerSkills(event);
        Event saved = eventRepository.save(event);
        log.info("Event id {} saved", event.getId());
        return saved;
    }

    public Event getEvent(Long id) {
        log.info("Getting Event id {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Event with id %d not found", id)));
        log.info("Event id {} found", id);
        return event;
    }

    public List<Event> getEventsByFilter(EventFiltersDto filters) {
        log.info("Filtering Events");
        List<Event> allEvents = eventRepository.findAll();

        List<Event> events = eventFilters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filters))
                .reduce(allEvents.stream(),
                        (stream, filter) -> filter.apply(stream, filters),
                        (s1, s2) -> s1)
                .toList();
        log.info("Events filtered");
        return events;
    }

    public void deleteEvent(Long eventId) {
        log.info("Deleting Event id {}", eventId);
        eventRepository.deleteById(eventId);
        log.info("Event id {} deleted", eventId);
    }

    public void updateEvent(Event inputEvent, Long ownerId, List<Long> relatedSkillIds) {
        log.info("Updating Event with owner id {}", ownerId);
        Event event = addUserAndSkillsForInputEvent(inputEvent, ownerId, relatedSkillIds);
        validateOwnerSkills(event);
        log.info("Owner skills correct. Try to find Event with owner id {}", ownerId);
        Event existingEvent = eventRepository.findById(event.getId())
                .orElseThrow(() -> new DataValidationException(
                        String.format("Event id %d not found", inputEvent.getId())));

        eventMapper.updateEntityFromDto(event, existingEvent);
        log.info("Event with id {} found, updating", event.getId());
        eventRepository.save(existingEvent);
        log.info("Event with id {} updated", event.getId());
    }

    public List<Event> getOwnedEvents(Long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);
        log.info("All Owned Events for User id {} found", userId);
        return events;
    }

    public List<Event> getParticipatedEvents(Long userId) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
        log.info("All Participated Events for User id {} found", userId);
        return events;
    }

    private void validateOwnerSkills(Event event) {
        User owner = event.getOwner();
        Set<Skill> ownerSkills = new HashSet<>(
                Optional.ofNullable(owner.getSkills()).orElse(new ArrayList<>()));
        List<Skill> relatedSkills = event.getRelatedSkills();
        if (!ownerSkills.containsAll(relatedSkills)) {
            throw new DataValidationException(
                    String.format("User with id %d don't have all related skills to create event id %d",
                            owner.getId(), event.getId()));
        }
        log.info("User with id {} have all related skills to create event id {}", owner.getId(), event.getId());
    }

    private Event addUserAndSkillsForInputEvent(Event inputEvent, Long ownerId, List<Long> relatedSkillIds) {
        User owner = userService.getUser(ownerId);
        List<Skill> skills = skillService.getSkills(relatedSkillIds);

        inputEvent.setOwner(owner);
        inputEvent.setRelatedSkills(skills);
        log.info("Created Event for Owner id {}. Was set skills with ids {}", ownerId, relatedSkillIds);
        return inputEvent;
    }
}

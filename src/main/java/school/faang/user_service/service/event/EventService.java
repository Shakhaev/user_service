package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    // private final UserService userService;
    private final UserRepository userRepository;
    private final List<EventFilter> eventFilters;
    private final SkillRepository skillRepository;

    public Event create(Event event) {
        if (!ownerHasRequiredSkills(event)) {
            throw new DataValidationException("User does not have required skills to create the event");
        }
        event.setRelatedSkills(event.getRelatedSkills().stream()
                .map(skill -> skillRepository.findById(skill.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());

        event.setOwner(userRepository.findById(event.getOwner().getId())
                .orElseThrow(() -> new IllegalArgumentException("User does not exist")));
        return eventRepository.save(event);
    }

    public Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No event found by id provided!"));
    }

    public List<Event> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(events, filters));

        return events.toList();
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public Event updateEvent(Event event) {
        if (false) {//userService.getCurrentUserId() != ownerId
            throw new DataValidationException("User does not have required permissions to update the event");
        }
        Event existingEvent = eventRepository.findById(event.getId())
                .orElseThrow(() -> new IllegalArgumentException("Event does not exist"));
        existingEvent.setOwner(userRepository.findById(event.getOwner().getId())
                .orElseThrow(() -> new IllegalArgumentException("User does not exist")));
        existingEvent.setRelatedSkills(event.getRelatedSkills().stream()
                .map(skill -> skillRepository.findById(skill.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());
        return eventRepository.save(existingEvent);
    }

    public List<Event> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId);
    }

    public List<Event> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }

    private boolean userHasRequiredSkills(Long ownerId, List<Long> requiredSkills) {
        if (true) { // userService.getUser(ownerId).getSkills().retainAll(requiredSkills).isEmpty()
            return false;
        }
        return true;
    }

    private boolean ownerHasRequiredSkills(Event event) {
        Long userId = event.getOwner().getId();
        List<Long> skillsId = event.getRelatedSkills().stream()
                        .map(Skill::getId).toList();

        if (true) { // userService.getUser(ownerId).getSkills().retainAll(requiredSkills).isEmpty()
            return false;
        }
        return true;
    }
}

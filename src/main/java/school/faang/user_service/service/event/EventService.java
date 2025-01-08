package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final List<EventFilter> eventFilters;
    private final EventMapper eventMapper;

    public Event create(Event event) {
        validateOwnerSkills(event);
        return eventRepository.save(event);
    }

    public Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Event with id %d not found", id)));
    }

    public List<Event> getEventsByFilter(EventFilters filters) {
        List<Event> allEvents = eventRepository.findAll();

        return eventFilters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filters))
                .reduce(allEvents.stream(),
                        (stream, filter) -> filter.apply(stream, filters),
                        (s1, s2) -> s1)
                .toList();
    }

    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    public void updateEvent(Event inputEvent) {
        validateOwnerSkills(inputEvent);
        Event existingEvent = eventRepository.findById(inputEvent.getId())
                .orElseThrow(() -> new DataValidationException(
                        String.format("Event id %d not found", inputEvent.getId())));

        eventMapper.updateEntityFromDto(inputEvent, existingEvent);
        eventRepository.save(existingEvent);
    }

    public List<Event> getOwnedEvents(Long userId) {
        return eventRepository.findAllByUserId(userId);
    }

    public List<Event> getParticipatedEvents(Long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
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
    }
}

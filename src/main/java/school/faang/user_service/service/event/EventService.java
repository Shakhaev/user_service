package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;


    public EventDto create(EventDto eventDto) {
        User owner = validateOwnerAndSkills(eventDto.ownerId(), eventDto.relatedSkills());

        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(owner);

        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDto(savedEvent);
    }

    public EventDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event not found with ID: " + eventId));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .filter(event -> filter.title() == null
                        || event.getTitle().toLowerCase().contains(filter.title().toLowerCase()))
                .filter(event -> filter.startDate() == null || !event.getStartDate().isBefore(filter.startDate()))
                .filter(event -> filter.endDate() == null || !event.getEndDate().isAfter(filter.endDate()))
                .filter(event -> filter.location() == null
                        || event.getLocation().equalsIgnoreCase(filter.location()))
                .filter(event -> filter.ownerId() == null || event.getOwner().getId().equals(filter.ownerId()))
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteEvent(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new DataValidationException("Event not found with ID: " + eventId);
        }
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        Event event = eventRepository.findById(eventDto.id())
                .orElseThrow(() -> new DataValidationException("Event not found with ID: " + eventDto.id()));

        if (!event.getOwner().getId().equals(eventDto.ownerId())) {
            throw new DataValidationException("Only the event owner can update the event.");
        }

        validateOwnerAndSkills(eventDto.ownerId(), eventDto.relatedSkills());

        event.setTitle(eventDto.title());
        event.setStartDate(eventDto.startDate());
        event.setEndDate(eventDto.endDate());
        event.setDescription(eventDto.description());
        event.setLocation(eventDto.location());
        event.setMaxAttendees(eventDto.maxAttendees());
        event.setRelatedSkills(eventDto.relatedSkills().stream()
                .map(skillId -> {
                    Skill skill = new Skill();
                    skill.setId(skillId);
                    return skill;
                })
                .collect(Collectors.toList()));

        Event updatedEvent = eventRepository.save(event);

        return eventMapper.toDto(updatedEvent);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> ownedEvents = eventRepository.findAllByUserId(userId);
        return ownedEvents.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> participatedEvents = eventRepository.findParticipatedEventsByUserId(userId);
        return participatedEvents.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    private User validateOwnerAndSkills(Long ownerId, List<Long> relatedSkills) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new DataValidationException("Owner not found with ID: " + ownerId));
        Set<Long> ownerSkillIds = owner.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        if (!ownerSkillIds.containsAll(relatedSkills)) {
            throw new DataValidationException("Owner does not possess all required skills for the event.");
        }

        return owner;
    }
}
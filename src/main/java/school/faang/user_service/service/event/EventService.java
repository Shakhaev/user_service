package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    // private final UserService userService;

    public EventDto create(EventDto eventDto) {
        if (!userHasRequiredSkills(eventDto.getOwnerId(), eventDto.getRelatedSkills())) {
            throw new DataValidationException("User does not have required skills to create the event");
        }
        Event savedEvent = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(savedEvent);
    }

    private boolean userHasRequiredSkills(Long ownerId, List<Long> requiredSkills) {
        if (true) { // userService.getUser(ownerId).getSkills().retainAll(requiredSkills).isEmpty()
            return false;
        }
        return true;
    }

    public EventDto getEvent(Long id) {
        Event foundEvent = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("No event found by id provided!"));

        return eventMapper.toDto(foundEvent);
    }

    public EventDto[] getEventsByFilter(EventFilterDto filter) {
        return (EventDto[]) eventRepository.findAll().stream()
                .filter(event -> filter.getTitle() == null || event.getTitle().contains(filter.getTitle()))
                .filter(event -> filter.getStartTime() == null || event.getStartDate().isBefore(filter.getStartTime()))
                .filter(event -> filter.getEndTime() == null || event.getEndDate().isAfter(filter.getEndTime()))
                .filter(event -> filter.getOwnerId() == null || event.getOwner().getId().equals(filter.getOwnerId()))
                .filter(event -> filter.getRelatedSkills() == null || event.getRelatedSkills().containsAll(filter.getRelatedSkills()))
                .filter(event -> filter.getLocation() == null || event.getLocation().contains(filter.getLocation()))
                .filter(event -> filter.getEventType() == null || event.getType().equals(filter.getEventType()))
                .filter(event -> filter.getEventStatus() == null || event.getStatus().equals(filter.getEventStatus()))
                .map(eventMapper::toDto)
                .toArray();
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public EventDto updateEvent(EventDto eventDto) {
        if (false) {//userService.getCurrentUserId() != ownerId
            throw new DataValidationException("User does not have required permissions to update the event");
        }

        Event updatedEvent = eventRepository.save(eventMapper.toEntity(eventDto));

        return eventMapper.toDto(updatedEvent);
    }

    public EventDto[] getOwnedEvents(long userId) {
        return (EventDto[]) eventRepository.findAllByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toArray();
    }

    public EventDto[] getParticipatedEvents(long userId) {
        return (EventDto[]) eventRepository.findParticipatedEventsByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toArray();
    }
}

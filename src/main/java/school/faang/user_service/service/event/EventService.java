package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public Event create(Event event) {
        log.info("Создаем событие: {}", event.getTitle());
        hasUserSkillsForEvent(event);
        Event savedEvent = eventRepository.save(event);
        log.info("Событие с ID: {}, успешно создано", savedEvent.getId());
        return savedEvent;
    }

    @Transactional(readOnly = true)
    public Event getEvent(Long eventId) {
        log.info("Ищем событие с ID: {}", eventId);
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие с ID " + eventId + " не найдено"));
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsByFilter(EventFilterDto filter) {
        List<Event> events = eventRepository.findAll();
        List<Event> eventsWithFilter = events.stream()
                .filter(event -> filter.getTitle() == null || event.getTitle().toLowerCase().contains(filter.getTitle().toLowerCase()))
                .filter(event -> filter.getOwnerId() == null || event.getOwner().getId().equals(filter.getOwnerId()))
                .filter(event -> filter.getLocation() == null || event.getLocation().equals(filter.getLocation()))
                .toList();
        log.info("По заданным фильтрам найдено: {} записей", eventsWithFilter.size());
        return eventsWithFilter;
    }

    @Transactional(readOnly = true)
    public List<Event> getOwnedEvents(Long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);
        log.info("Для пользователя с ID: {}, найдено {} записей которые он создал", userId, events.size());
        return events;
    }

    @Transactional(readOnly = true)
    public List<Event> getParticipatedEvents(Long userId) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
        log.info("Для пользователя с ID: {}, найдено {} записей в которых он принимает участие", userId, events.size());
        return events;
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        log.info("Удаляем событие с ID: {}", eventId);
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Событие с ID " + eventId + " не найдено");
        }
        eventRepository.deleteById(eventId);
        log.info("Удалили событие с ID: {}", eventId);
    }

    @Transactional
    public Event updateEvent(Event event) {
        log.info("Обновляем событие с ID: {}", event.getId());

        Event existingEvent = getExistingEvent(event.getId());
        validateEventOwnership(existingEvent, event.getOwner().getId());
        hasUserSkillsForEvent(event);

        existingEvent.setTitle(event.getTitle());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setStartDate(event.getStartDate());
        existingEvent.setEndDate(event.getEndDate());
        existingEvent.setLocation(event.getLocation());
        existingEvent.setMaxAttendees(event.getMaxAttendees());
        existingEvent.setRelatedSkills(event.getRelatedSkills());

        Event updatedEvent = eventRepository.save(existingEvent);
        log.info("Событие с ID: {} успешно обновлено", updatedEvent.getId());
        return updatedEvent;
    }

    private Event getExistingEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие с ID " + eventId + " не найдено"));
    }

    private void validateEventOwnership(Event existingEvent, Long ownerId) {
        if (!existingEvent.getOwner().getId().equals(ownerId)) {
            throw new DataValidationException("Пользователь с ID " + ownerId + " не является владельцем события");
        }
    }

    private void hasUserSkillsForEvent(Event event) {
        log.info("Проверяем навыки пользователя с ID: {}", event.getOwner().getId());
        Long userId = event.getOwner().getId();
        User user = userRepository.findByIdWithSkills(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID: " + userId + " не найден"));
        Set<String> userSkills = user.getSkills().stream()
                .map(Skill::getTitle)
                .collect(Collectors.toSet());
        boolean hasAllSkills = event.getRelatedSkills().stream()
                .map(Skill::getTitle)
                .allMatch(userSkills::contains);
        if (!hasAllSkills) {
            throw new DataValidationException("Пользователь с ID: " + userId + " не имеет необходимых навыков");
        }
    }
}

package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.annotation.publisher.PublishEvent;
import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.event.exceptions.EventNotFoundException;
import school.faang.user_service.exception.event.exceptions.InsufficientSkillsException;
import school.faang.user_service.service.event.filters.EventFilter;
import school.faang.user_service.service.user.UserDomainService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static school.faang.user_service.entity.event.EventStatus.IN_PROGRESS;
import static school.faang.user_service.entity.event.EventStatus.PLANNED;
import static school.faang.user_service.enums.publisher.PublisherType.EVENT_START;

@RequiredArgsConstructor
@Slf4j
@Service
public class EventServiceImpl implements EventService {
    private final EventDomainService eventDomainService;
    private final UserDomainService userDomainService;
    private final List<EventFilter> eventFilters;

    @Override
    @Transactional
    public Event create(Event event) {
        log.info("Создание события с title: {}", event.getTitle());
        validateUserSkills(event);
        return eventDomainService.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Event getEvent(Long eventId) {
        log.info("Поиск события с ID: {}", eventId);
        return eventDomainService.findById(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEventsByFilter(EventFilters eventFilters) {
        log.info("Фильтрация событий по критериям: {}", eventFilters);
        List<Event> events = eventDomainService.findAll();

        List<Event> filteredEvents = this.eventFilters.stream()
                .filter(filter -> filter.isApplicable(eventFilters))
                .reduce(events.stream(),
                        (stream, filter) -> filter.apply(stream, eventFilters),
                        (s1, s2) -> s1)
                .toList();

        log.info("Найдено {} событий по заданным критериям", filteredEvents.size());

        return filteredEvents;
    }

    @Override
    @Transactional
    public Event updateEvent(Event event) {
        log.info("Обновление события с ID: {}", event.getId());
        validateUserSkills(event);

        Event existingEvent = getEvent(event.getId());

        existingEvent.setTitle(event.getTitle());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setStartDate(event.getStartDate());
        existingEvent.setEndDate(event.getEndDate());
        existingEvent.setLocation(event.getLocation());
        existingEvent.setMaxAttendees(event.getMaxAttendees());
        existingEvent.setOwner(event.getOwner());
        existingEvent.setRelatedSkills(event.getRelatedSkills());
        existingEvent.setType(event.getType());
        existingEvent.setStatus(event.getStatus());

        return eventDomainService.save(existingEvent);
    }

    @Transactional(readOnly = true)
    public Integer getSubscribersCount(Event event) {
        Long ownerId = event.getOwner().getId();
        log.info("Получение количества подписчиков у пользователя с ID: {}", ownerId);
        return userDomainService.countFollowersByUserId(ownerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getOwnedEvents(Long userId) {
        log.info("Получение событий, созданных пользователем с ID: {}", userId);
        return eventDomainService.findAllByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getParticipatedEvents(Long userId) {
        log.info("Получение событий, в которых участвовал пользователь с ID: {}", userId);
        return eventDomainService.findParticipatedEventsByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteEvent(Long eventId) {
        log.info("Удаление события с ID: {}", eventId);
        try {
            eventDomainService.deleteById(eventId);
            log.info("Событие с ID {} успешно удалено", eventId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка удаления. Событие с ID {} не найдено", eventId);
            throw new EventNotFoundException(eventId);
        }
    }

    @PublishEvent(type = EVENT_START)
    @Override
    @Transactional
    public List<Event> startEventsFromPeriod(LocalDateTime from, LocalDateTime to) {
        List<Event> events = eventDomainService.findAllByStatusAndStartDateBetween(PLANNED, from, to);

        events.forEach(event -> event.setStatus(IN_PROGRESS));
        return eventDomainService.saveAll(events);
    }

    private void validateUserSkills(Event event) {
        log.info("Проверка навыков пользователя с ID: {}", event.getOwner().getId());
        User user = loadUserById(event.getOwner().getId());
        List<Skill> relatedSkills = event.getRelatedSkills();
        List<Skill> userSkills = Optional.ofNullable(user.getSkills()).orElse(new ArrayList<>());
        if (!userSkills.containsAll(relatedSkills)) {
            log.error("У пользователя с ID {} недостаточно навыков для события", user.getId());
            throw new InsufficientSkillsException(user.getId());
        }
    }

    private User loadUserById(Long id) {
        return userDomainService.findById(id);
    }
}
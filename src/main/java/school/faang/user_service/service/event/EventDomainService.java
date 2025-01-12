package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.event.exceptions.EventNotFoundException;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EventDomainService {
    private final EventRepository eventRepository;

    @Transactional
    public Event save(Event event) {
        return eventRepository.save(event);
    }

    @Transactional
    public void delete(Event event) {
        eventRepository.delete(event);
    }

    @Transactional(readOnly = true)
    public Event findById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Event> findAllSortedByPromotedEventsPerPage(Long offset, Long limit) {
        return eventRepository.findAllSortedByPromotedEventsPerPage(offset, limit);
    }
}

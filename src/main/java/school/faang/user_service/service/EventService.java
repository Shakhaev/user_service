package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    @Transactional
    public void deactivateEventsByUser(Long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);

        events.forEach(event -> {
            event.setStatus(EventStatus.COMPLETED);
            eventRepository.save(event);
            eventRepository.delete(event);
        });
    }
}

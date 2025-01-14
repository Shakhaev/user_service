package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    @Transactional
    public void deactivateEventsByUser(Long userId) {
        var events = eventRepository.findAllByUserId(userId);

        events.forEach(event -> {
            event.setStatus(EventStatus.COMPLETED);
            eventRepository.save(event);
            eventRepository.delete(event);
        });
    }
}

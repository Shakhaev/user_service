package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.repository.event.EventRepository;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository repository;

    @Transactional
    public void removeEvent(Long eventId) {
        repository.deleteById(eventId);
    }
}

package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.event.EventRepository;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository repository;

    public void removeEvent(long eventId) {
        repository.deleteById(eventId);
    }
}

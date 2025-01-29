package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public void deactivateEventsByUserId(long userId) {
        List<Long> eventListId = eventRepository.findAllByUserId(userId)
                .stream()
                .filter(event -> event.getStatus() == EventStatus.PLANNED)
                .map(Event::getId)
                .toList();
        if (!eventListId.isEmpty()) {
            eventRepository.deleteUserParticipationByEventId(eventListId);
            eventRepository.deleteAllById(eventListId);
        }
    }
}

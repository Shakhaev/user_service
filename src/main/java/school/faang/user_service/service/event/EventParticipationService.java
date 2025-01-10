package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(long eventId, long userId) {
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        boolean isRegistered = users.stream()
                .anyMatch(user -> user.getId() == userId);
        if (isRegistered) {
            throw new IllegalArgumentException("User " + userId + " already registered to the event " + eventId);
        }
        eventParticipationRepository.register(eventId, userId);
        log.info("User: {} registered to the event: {}", userId, eventId);
    }

    public void unregisterParticipant(long eventId, long userId) {
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);

        boolean isRegistered = users.stream()
                .anyMatch(user -> user.getId() == userId);
        if (isRegistered) {
            eventParticipationRepository.unregister(eventId, userId);
            log.info("Registration is cancelled to user: {} from the event: {}", userId, eventId);
        } else {
            throw new IllegalArgumentException("User " + userId + " is not registered to the event " + eventId);
        }
    }

    public List<User> getParticipant(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
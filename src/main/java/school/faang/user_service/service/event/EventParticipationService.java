package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(long eventId, long userId) {
        try {
            boolean isAlreadyRegistered = eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                    .anyMatch(user -> Long.valueOf(user.getId()).equals(userId));

            if (isAlreadyRegistered) {
                throw new RuntimeException("User is already registered for the event.");
            }
            eventParticipationRepository.register(eventId, userId);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while registering the participant", e);
        }
    }

    public void unregisterParticipant(long eventId, long userId) {
        try {
            boolean isAlreadyRegistered = eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                    .anyMatch(user -> Long.valueOf(user.getId()).equals(userId));

            if (!isAlreadyRegistered) {
                throw new RuntimeException("User wasn't registered on the event yet");
            }
            eventParticipationRepository.unregister(eventId, userId);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while unregistering the participant", e);
        }
    }

    public List<User> getParticipant(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}

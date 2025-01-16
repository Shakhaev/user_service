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

    public void registerParticipant(long eventId, long userId) throws Exception {
        try {
            if (eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                    .noneMatch(user -> user.getId() == userId)) {
                eventParticipationRepository.register(eventId, userId);
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public void unregisterParticipant(long eventId, long userId) throws Exception {
        try {
            if (eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                    .anyMatch(user -> user.getId() == userId)) {
                eventParticipationRepository.unregister(eventId, userId);
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public List<User> getParticipant(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}

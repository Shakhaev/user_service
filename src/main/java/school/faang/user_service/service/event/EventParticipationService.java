package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(long eventId, long userId) {
        List<User> allParticipants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        Optional<User> participant = allParticipants.stream()
                .filter((user -> user.getId().equals(userId)))
                .findFirst();
        if (participant.isEmpty()) {
            eventParticipationRepository.register(eventId, userId);
        } else {
            throw new IllegalStateException(
                    String.format("User with ID %d is already registered for event with ID %d", userId, eventId)
            );
        }
    }

    public void unregisterParticipant(long eventId, long userId){
        List<User> allParticipants = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        Optional<User> participant = allParticipants.stream()
                .filter((user -> user.getId().equals(userId)))
                .findFirst();
        if (participant.isPresent()) {
            eventParticipationRepository.unregister(eventId, userId);
        } else {
            throw new IllegalStateException(
                    String.format("User with ID %d is not registered for event with ID %d", userId, eventId)
            );
        }
    }

    public List<User> getParticipants(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    public int getParticipantsCount(long eventId){
        return eventParticipationRepository.countParticipants(eventId);
    }
}
